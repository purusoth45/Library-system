package com.library.management.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;

import org.springframework.security.web.SecurityFilterChain;


import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity


public class SecurityConfig {

    // ==========================================
    // JWT SECRET
    // ==========================================

    @Value("${jwt.secret}")
    private String jwtSecret;


    // ==========================================
    // CORS ORIGINS
    // ==========================================
    // Default is EMPTY on purpose — no hardcoded frontend URL (e.g. no
    // localhost:3000 fallback). If this service sits behind a gateway/BFF
    // that owns CORS, leave the property unset and CORS registration is
    // skipped entirely (see corsConfigurationSource() below).
    // If direct browser calls to THIS service are ever needed, set
    // app.cors.allowed-origins explicitly per environment
    // (application.yml or APP_CORS_ALLOWED_ORIGINS env var).
    @Value("${app.cors.allowed-origins:}")
    private String allowedOrigins;


    // ==========================================
    // PASSWORD ENCODER
    // ==========================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // ==========================================
    // SECRET KEY
    // ==========================================

    @Bean
    public SecretKey secretKey() {

        return new SecretKeySpec(
                jwtSecret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
    }


    // ==========================================
    // JWT ENCODER
    // ==========================================

    @Bean
    public JwtEncoder jwtEncoder() {

        return new NimbusJwtEncoder(
                new ImmutableSecret<>(
                        secretKey().getEncoded()
                )
        );
    }


    // ==========================================
    // JWT DECODER
    // ==========================================

    @Bean
    public JwtDecoder jwtDecoder() {

        return NimbusJwtDecoder
                .withSecretKey(secretKey())
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }


    // ==========================================
    // JWT ROLE CONVERTER
    // ==========================================

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            String role =
                    jwt.getClaimAsString("role");

            List<GrantedAuthority> authorities =
                    new ArrayList<>();

            if (role != null && !role.isBlank()) {

                authorities.add(
                        new SimpleGrantedAuthority(
                                "ROLE_" + role
                        )
                );
            }

            return authorities;
        });

        return converter;
    }


    // ==========================================
    // BEARER TOKEN RESOLVER
    // ==========================================

    @Bean
    public BearerTokenResolver bearerTokenResolver() {

        DefaultBearerTokenResolver defaultResolver =
                new DefaultBearerTokenResolver();

        return new BearerTokenResolver() {

            @Override
            public String resolve(
                    HttpServletRequest request
            ) {

                String uri =
                        request.getRequestURI();

                // Login does not require JWT
                if (uri.equals("/api/auth/login")) {
                    return null;
                }

                return defaultResolver.resolve(request);
            }
        };
    }


    // ==========================================
    // SECURITY FILTER CHAIN
    // ==========================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        CorsConfigurationSource corsSource =
                corsConfigurationSource();

        http

                // CSRF disabled for REST API
                .csrf(csrf ->
                        csrf.disable()
                )

                // CORS — only registered if origins are actually configured.
                // If corsSource is null (no origins set), Spring Security's
                // CORS filter is skipped, i.e. this service does not
                // handle browser CORS at all (expected when a gateway/BFF
                // in front of it owns that responsibility).
                .cors(cors -> {
                    if (corsSource != null) {
                        cors.configurationSource(corsSource);
                    } else {
                        cors.disable();
                    }
                })

                // JWT stateless session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Authorization
                .authorizeHttpRequests(auth -> auth

                        // CORS preflight
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // Login
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/login"
                        ).permitAll()

                        // Auth APIs
                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        // Other APIs
                        .anyRequest().authenticated()
                )

                // JWT Resource Server
                .oauth2ResourceServer(oauth2 ->
                        oauth2
                                .bearerTokenResolver(
                                        bearerTokenResolver()
                                )
                                .jwt(jwt ->
                                        jwt.jwtAuthenticationConverter(
                                                jwtAuthenticationConverter()
                                        )
                                )
                );

        return http.build();
    }


    // ==========================================
    // CORS CONFIGURATION
    // ==========================================
    // Returns null when no origins are configured, meaning this service
    // will NOT accept direct cross-origin browser requests. That is the
    // intended state when a gateway/BFF sits in front of the frontend.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        List<String> origins =
                Arrays.stream(
                                allowedOrigins.split(",")
                        )
                        .map(String::trim)
                        .filter(
                                origin ->
                                        !origin.isBlank()
                        )
                        .toList();

        if (origins.isEmpty()) {
            // No direct frontend origins configured — CORS not needed here.
            return null;
        }

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                origins
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setExposedHeaders(
                List.of("Authorization")
        );

        configuration.setAllowCredentials(
                true
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}
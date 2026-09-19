package com.library.management.service;

import com.library.management.entity.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;

    private final long expiration;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${jwt.expiration:86400000}") long expiration
    ) {
        this.jwtEncoder = jwtEncoder;

        // Safety fallback
        this.expiration =
                expiration > 0
                        ? expiration
                        : 86400000L;
    }

    public String generateToken(User user) {

        Instant now = Instant.now();

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .issuer("library-management")
                        .subject(user.getEmail())
                        .issuedAt(now)
                        .expiresAt(
                                now.plusMillis(expiration)
                        )
                        .claim(
                                "userId",
                                user.getId()
                        )
                        .claim(
                                "name",
                                user.getName()
                        )
                        .claim(
                                "role",
                                user.getRole().name()
                        )
                        .build();

        JwsHeader header =
                JwsHeader
                        .with(MacAlgorithm.HS256)
                        .build();

        return jwtEncoder
                .encode(
                        JwtEncoderParameters.from(
                                header,
                                claims
                        )
                )
                .getTokenValue();
    }
}
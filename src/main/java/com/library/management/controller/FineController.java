package com.library.management.controller;

import com.library.management.dto.FineResponse;
import com.library.management.entity.Fine;
import com.library.management.service.FineService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
public class FineController {

    private final FineService fineService;

    public FineController(FineService fineService) {
        this.fineService = fineService;
    }

    // ==========================================
    // GET ALL FINES
    // ==========================================

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FineResponse>> getAllFines() {

        return ResponseEntity.ok(
                fineService.getAllFines()
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    // ==========================================
    // GET FINE BY ID
    // ==========================================

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FineResponse> getFineById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                toResponse(
                        fineService.getFineById(id)
                )
        );
    }

    // ==========================================
    // CALCULATE FINE
    // ==========================================

    @PostMapping("/calculate/{issueId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FineResponse> calculateFine(
            @PathVariable Long issueId
    ) {

        Fine fine =
                fineService.calculateFine(issueId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(fine));
    }

    // ==========================================
    // FINE BY ISSUE
    // ==========================================

    @GetMapping("/issue/{issueId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FineResponse> getFineByIssue(
            @PathVariable Long issueId
    ) {

        return ResponseEntity.ok(
                toResponse(
                        fineService.getFineByIssueId(issueId)
                )
        );
    }

    // ==========================================
    // UNPAID FINES
    // ==========================================

    @GetMapping("/unpaid")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FineResponse>> getUnpaidFines() {

        return ResponseEntity.ok(
                fineService.getUnpaidFines()
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    // ==========================================
    // PAID FINES
    // ==========================================

    @GetMapping("/paid")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FineResponse>> getPaidFines() {

        return ResponseEntity.ok(
                fineService.getPaidFines()
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    // ==========================================
    // PAY FINE
    // ==========================================

    @PutMapping("/{id}/pay")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FineResponse> payFine(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                toResponse(
                        fineService.payFine(id)
                )
        );
    }

    // ==========================================
    // ENTITY → DTO
    // ==========================================

    private FineResponse toResponse(Fine fine) {

        var issue = fine.getBookIssue();

        return new FineResponse(

                fine.getId(),

                issue != null
                        ? issue.getId()
                        : null,

                issue != null && issue.getBook() != null
                        ? issue.getBook().getId()
                        : null,

                issue != null && issue.getBook() != null
                        ? issue.getBook().getTitle()
                        : null,

                issue != null && issue.getUser() != null
                        ? issue.getUser().getId()
                        : null,

                issue != null && issue.getUser() != null
                        ? issue.getUser().getName()
                        : null,

                issue != null && issue.getUser() != null
                        ? issue.getUser().getEmail()
                        : null,

                issue != null
                        ? issue.getDueDate()
                        : null,

                issue != null
                        ? issue.getReturnDate()
                        : null,

                fine.getAmount(),
                fine.getStatus(),
                fine.getPaidDate()
        );
    }
}
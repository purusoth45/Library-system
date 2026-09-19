package com.library.management.controller;

import com.library.management.dto.BookIssueResponse;
import com.library.management.entity.BookIssue;
import com.library.management.entity.IssueStatus;
import com.library.management.service.BookIssueService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import com.library.management.entity.User;
import com.library.management.service.UserService;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/issues")
public class BookIssueController {

    private final BookIssueService bookIssueService;

    public BookIssueController(
            BookIssueService bookIssueService
    ) {
        this.bookIssueService = bookIssueService;
    }

    // ==========================================
    // GET ALL ISSUES
    // ==========================================

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public ResponseEntity<List<BookIssueResponse>> getAllIssues() {

        return ResponseEntity.ok(
                bookIssueService.getAllIssues()
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    // ==========================================
    // GET ISSUE BY ID
    // ==========================================

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public ResponseEntity<BookIssueResponse> getIssueById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                toResponse(
                        bookIssueService.getIssueById(id)
                )
        );
    }

    // ==========================================
    // ISSUE BOOK
    // ==========================================

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookIssueResponse> issueBook(
            @RequestParam Long bookId,
            @RequestParam Long userId,
            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate dueDate
    ) {

        BookIssue issue =
                bookIssueService.issueBook(
                        bookId,
                        userId,
                        dueDate
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(issue));
    }

    // ==========================================
    // RETURN BOOK
    // ==========================================

    @PutMapping("/{id}/return")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookIssueResponse> returnBook(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                toResponse(
                        bookIssueService.returnBook(id)
                )
        );
    }

    // ==========================================
    // ISSUES BY USER
    // ==========================================

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public ResponseEntity<List<BookIssueResponse>> getIssuesByUser(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                bookIssueService.getIssuesByUser(userId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    // ==========================================
    // ISSUES BY BOOK
    // ==========================================

    @GetMapping("/book/{bookId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public ResponseEntity<List<BookIssueResponse>> getIssuesByBook(
            @PathVariable Long bookId
    ) {

        return ResponseEntity.ok(
                bookIssueService.getIssuesByBook(bookId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    // ==========================================
    // ISSUES BY STATUS
    // ==========================================

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public ResponseEntity<List<BookIssueResponse>> getIssuesByStatus(
            @PathVariable IssueStatus status
    ) {

        return ResponseEntity.ok(
                bookIssueService.getIssuesByStatus(status)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    // ==========================================
    // OVERDUE ISSUES
    // ==========================================

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public ResponseEntity<List<BookIssueResponse>> getOverdueIssues() {

        return ResponseEntity.ok(
                bookIssueService.getOverdueIssues()
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    // ==========================================
    // ENTITY → DTO
    // ==========================================

    private BookIssueResponse toResponse(
            BookIssue issue
    ) {

        return new BookIssueResponse(
                issue.getId(),

                issue.getBook() != null
                        ? issue.getBook().getId()
                        : null,

                issue.getBook() != null
                        ? issue.getBook().getTitle()
                        : null,

                issue.getUser() != null
                        ? issue.getUser().getId()
                        : null,

                issue.getUser() != null
                        ? issue.getUser().getName()
                        : null,

                issue.getUser() != null
                        ? issue.getUser().getEmail()
                        : null,

                issue.getIssueDate(),
                issue.getDueDate(),
                issue.getReturnDate(),
                issue.getStatus()
        );
    }
}
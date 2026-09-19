package com.library.management.controller;

import com.library.management.dto.BookResponse;
import com.library.management.entity.Book;
import com.library.management.service.BookService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // ==========================================
    // GET ALL BOOKS
    // ==========================================

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public ResponseEntity<List<BookResponse>> getAllBooks() {

        List<BookResponse> books =
                bookService.getAllBooks()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(books);
    }

    // ==========================================
    // GET BOOK BY ID
    // ==========================================

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public ResponseEntity<BookResponse> getBookById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                toResponse(
                        bookService.getBookById(id)
                )
        );
    }

    // ==========================================
    // CREATE BOOK
    // ==========================================

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> createBook(
            @Valid @RequestBody Book book
    ) {

        Book savedBook =
                bookService.createBook(book);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedBook));
    }

    // ==========================================
    // UPDATE BOOK
    // ==========================================

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody Book book
    ) {

        Book updatedBook =
                bookService.updateBook(
                        id,
                        book
                );

        return ResponseEntity.ok(
                toResponse(updatedBook)
        );
    }

    // ==========================================
    // DELETE BOOK
    // ==========================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id
    ) {

        bookService.deleteBook(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    // ==========================================
    // SEARCH BY TITLE
    // ==========================================

    @GetMapping("/search/title")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public ResponseEntity<List<BookResponse>> searchByTitle(
            @RequestParam String title
    ) {

        return ResponseEntity.ok(
                bookService.searchByTitle(title)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    // ==========================================
    // SEARCH BY AUTHOR
    // ==========================================

    @GetMapping("/search/author")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public ResponseEntity<List<BookResponse>> searchByAuthor(
            @RequestParam String author
    ) {

        return ResponseEntity.ok(
                bookService.searchByAuthor(author)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    // ==========================================
    // BOOKS BY CATEGORY
    // ==========================================

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public ResponseEntity<List<BookResponse>> getBooksByCategory(
            @PathVariable Long categoryId
    ) {

        return ResponseEntity.ok(
                bookService.getBooksByCategory(categoryId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    // ==========================================
    // ENTITY → DTO
    // ==========================================

    private BookResponse toResponse(Book book) {

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getTotalCopies(),
                book.getAvailableCopies(),
                book.getCategory() != null
                        ? book.getCategory().getId()
                        : null,
                book.getCategory() != null
                        ? book.getCategory().getName()
                        : null
        );
    }
}
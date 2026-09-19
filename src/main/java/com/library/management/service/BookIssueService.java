package com.library.management.service;

import com.library.management.entity.Book;
import com.library.management.entity.BookIssue;
import com.library.management.entity.IssueStatus;
import com.library.management.entity.User;
import com.library.management.repository.BookIssueRepository;
import com.library.management.repository.BookRepository;
import com.library.management.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookIssueService {

    private final BookIssueRepository bookIssueRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BookIssueService(BookIssueRepository bookIssueRepository,
                            BookRepository bookRepository,
                            UserRepository userRepository) {
        this.bookIssueRepository = bookIssueRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // Get all issued books
    public List<BookIssue> getAllIssues() {
        return bookIssueRepository.findAll();
    }

    // Get issue by ID
    public BookIssue getIssueById(Long id) {
        return bookIssueRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Book issue not found with id: " + id
                        ));
    }

    // Issue a book
    @Transactional
    public BookIssue issueBook(Long bookId, Long userId, LocalDate dueDate) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Book not found with id: " + bookId
                        ));

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + userId
                        ));

        // Check available copies
        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException(
                    "Book is currently not available"
            );
        }

        // Validate due date
        if (dueDate == null || dueDate.isBefore(LocalDate.now())) {
            throw new RuntimeException(
                    "Due date must be today or a future date"
            );
        }

        // Decrease available copies
        book.setAvailableCopies(
                book.getAvailableCopies() - 1
        );

        bookRepository.save(book);

        // Create issue record
        BookIssue issue = new BookIssue();

        issue.setBook(book);
        issue.setUser(user);
        issue.setIssueDate(LocalDate.now());
        issue.setDueDate(dueDate);
        issue.setStatus(IssueStatus.ISSUED);

        return bookIssueRepository.save(issue);
    }

    // Return a book
    @Transactional
    public BookIssue returnBook(Long issueId) {

        BookIssue issue = getIssueById(issueId);

        // Prevent returning the same book twice
        if (issue.getStatus() == IssueStatus.RETURNED) {
            throw new RuntimeException(
                    "Book has already been returned"
            );
        }

        Book book = issue.getBook();

        // Increase available copies
        book.setAvailableCopies(
                book.getAvailableCopies() + 1
        );

        bookRepository.save(book);

        // Set return details
        issue.setReturnDate(LocalDate.now());
        issue.setStatus(IssueStatus.RETURNED);

        return bookIssueRepository.save(issue);
    }

    // Get issues for a particular user
    public List<BookIssue> getIssuesByUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException(
                    "User not found with id: " + userId
            );
        }

        return bookIssueRepository.findByUserId(userId);
    }

    // Get issues for a particular book
    public List<BookIssue> getIssuesByBook(Long bookId) {

        if (!bookRepository.existsById(bookId)) {
            throw new RuntimeException(
                    "Book not found with id: " + bookId
            );
        }

        return bookIssueRepository.findByBookId(bookId);
    }

    // Get issues by status
    public List<BookIssue> getIssuesByStatus(IssueStatus status) {
        return bookIssueRepository.findByStatus(status);
    }

    // Find overdue books
    public List<BookIssue> getOverdueIssues() {

        List<BookIssue> issues =
                bookIssueRepository.findByStatus(IssueStatus.ISSUED);

        LocalDate today = LocalDate.now();

        for (BookIssue issue : issues) {

            if (issue.getDueDate().isBefore(today)) {
                issue.setStatus(IssueStatus.OVERDUE);
            }
        }

        return bookIssueRepository.saveAll(issues);
    }
}
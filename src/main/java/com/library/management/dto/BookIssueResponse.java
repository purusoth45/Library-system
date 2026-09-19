package com.library.management.dto;

import com.library.management.entity.IssueStatus;

import java.time.LocalDate;

public class BookIssueResponse {

    private Long id;

    private Long bookId;
    private String bookTitle;

    private Long userId;
    private String userName;
    private String userEmail;

    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    private IssueStatus status;

    public BookIssueResponse() {
    }

    public BookIssueResponse(
            Long id,
            Long bookId,
            String bookTitle,
            Long userId,
            String userName,
            String userEmail,
            LocalDate issueDate,
            LocalDate dueDate,
            LocalDate returnDate,
            IssueStatus status
    ) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }
}
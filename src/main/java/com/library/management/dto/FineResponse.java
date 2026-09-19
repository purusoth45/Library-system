package com.library.management.dto;

import com.library.management.entity.FineStatus;

import java.time.LocalDate;

public class FineResponse {

    private Long id;

    private Long issueId;

    private Long bookId;
    private String bookTitle;

    private Long userId;
    private String userName;
    private String userEmail;

    private LocalDate dueDate;
    private LocalDate returnDate;

    private Double amount;
    private FineStatus status;
    private LocalDate paidDate;

    public FineResponse() {
    }

    public FineResponse(
            Long id,
            Long issueId,
            Long bookId,
            String bookTitle,
            Long userId,
            String userName,
            String userEmail,
            LocalDate dueDate,
            LocalDate returnDate,
            Double amount,
            FineStatus status,
            LocalDate paidDate
    ) {
        this.id = id;
        this.issueId = issueId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.amount = amount;
        this.status = status;
        this.paidDate = paidDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public FineStatus getStatus() {
        return status;
    }

    public void setStatus(FineStatus status) {
        this.status = status;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }
}
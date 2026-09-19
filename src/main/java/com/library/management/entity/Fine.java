package com.library.management.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "fines")
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "issue_id", nullable = false, unique = true)
    private BookIssue bookIssue;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FineStatus status;

    private LocalDate paidDate;

    public Fine() {
    }

    public Fine(Long id, BookIssue bookIssue, Double amount,
                FineStatus status, LocalDate paidDate) {
        this.id = id;
        this.bookIssue = bookIssue;
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

    public BookIssue getBookIssue() {
        return bookIssue;
    }

    public void setBookIssue(BookIssue bookIssue) {
        this.bookIssue = bookIssue;
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
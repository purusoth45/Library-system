package com.library.management.service;

import com.library.management.entity.BookIssue;
import com.library.management.entity.Fine;
import com.library.management.entity.FineStatus;
import com.library.management.entity.IssueStatus;
import com.library.management.repository.BookIssueRepository;
import com.library.management.repository.FineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class FineService {

    private static final double FINE_PER_DAY = 10.0;

    private final FineRepository fineRepository;
    private final BookIssueRepository bookIssueRepository;

    public FineService(FineRepository fineRepository,
                       BookIssueRepository bookIssueRepository) {
        this.fineRepository = fineRepository;
        this.bookIssueRepository = bookIssueRepository;
    }

    // Get all fines
    public List<Fine> getAllFines() {
        return fineRepository.findAll();
    }

    // Get fine by ID
    public Fine getFineById(Long id) {
        return fineRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Fine not found with id: " + id
                        ));
    }

    // Calculate fine for a book issue
    @Transactional
    public Fine calculateFine(Long issueId) {

        BookIssue issue = bookIssueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Book issue not found with id: " + issueId
                        ));

        // Check whether fine already exists
        Fine existingFine = fineRepository
                .findByBookIssueId(issueId)
                .orElse(null);

        LocalDate endDate;

        if (issue.getReturnDate() != null) {
            endDate = issue.getReturnDate();
        } else {
            endDate = LocalDate.now();
        }

        // Calculate late days
        long lateDays = ChronoUnit.DAYS.between(
                issue.getDueDate(),
                endDate
        );

        // No fine if returned on or before due date
        if (lateDays <= 0) {

            if (existingFine != null) {
                return existingFine;
            }

            Fine fine = new Fine();
            fine.setBookIssue(issue);
            fine.setAmount(0.0);
            fine.setStatus(FineStatus.PAID);

            return fineRepository.save(fine);
        }

        double amount = lateDays * FINE_PER_DAY;

        if (existingFine != null) {

            existingFine.setAmount(amount);

            if (existingFine.getStatus() == null) {
                existingFine.setStatus(FineStatus.UNPAID);
            }

            return fineRepository.save(existingFine);
        }

        Fine fine = new Fine();

        fine.setBookIssue(issue);
        fine.setAmount(amount);
        fine.setStatus(FineStatus.UNPAID);

        return fineRepository.save(fine);
    }

    // Get fine for a particular issue
    public Fine getFineByIssueId(Long issueId) {

        return fineRepository.findByBookIssueId(issueId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Fine not found for issue id: " + issueId
                        ));
    }

    // Get all unpaid fines
    public List<Fine> getUnpaidFines() {
        return fineRepository.findByStatus(FineStatus.UNPAID);
    }

    // Get all paid fines
    public List<Fine> getPaidFines() {
        return fineRepository.findByStatus(FineStatus.PAID);
    }

    // Mark fine as paid
    @Transactional
    public Fine payFine(Long fineId) {

        Fine fine = getFineById(fineId);

        if (fine.getStatus() == FineStatus.PAID) {
            throw new RuntimeException(
                    "Fine has already been paid"
            );
        }

        fine.setStatus(FineStatus.PAID);
        fine.setPaidDate(LocalDate.now());

        return fineRepository.save(fine);
    }
}
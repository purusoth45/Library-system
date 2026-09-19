package com.library.management.repository;

import com.library.management.entity.BookIssue;
import com.library.management.entity.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {

    List<BookIssue> findByUserId(Long userId);

    List<BookIssue> findByBookId(Long bookId);

    List<BookIssue> findByStatus(IssueStatus status);
}
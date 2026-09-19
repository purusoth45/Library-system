package com.library.management.repository;

import com.library.management.entity.Fine;
import com.library.management.entity.FineStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FineRepository extends JpaRepository<Fine, Long> {

    Optional<Fine> findByBookIssueId(Long issueId);

    List<Fine> findByStatus(FineStatus status);
}
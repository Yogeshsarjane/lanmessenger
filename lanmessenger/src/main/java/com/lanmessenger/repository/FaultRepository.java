package com.lanmessenger.repository;

import com.lanmessenger.model.Fault;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaultRepository extends JpaRepository<Fault, Long> {
    // Find all faults, ordered by the newest ones first
    List<Fault> findAllByOrderBySubmissionTimestampDesc();
}
package com.artaxer.tinybank.model.repository;


import com.artaxer.tinybank.model.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    Optional<TransactionEntity> findByTrackingNumber(String trackingNumber);

    List<TransactionEntity> findByAccountNumber(String accountNumber);
    Page<TransactionEntity> findByAccountNumber(String accountNumber, Pageable pageable);
}

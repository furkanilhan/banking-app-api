package com.furkan.banking.repository;

import com.furkan.banking.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.from.id = :accountId OR t.to.id = :accountId")
    Page<Transaction> findAllByAccountId(UUID accountId, Pageable pageable);
}

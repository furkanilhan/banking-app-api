package com.furkan.banking.repository;

import com.furkan.banking.model.Account;
import com.furkan.banking.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID>, JpaSpecificationExecutor<Account> {
    boolean existsByNameAndUserAndIsDeletedFalse(String name, User user);
    Optional<Account> findByIdAndUserAndIsDeletedFalse(UUID id, User user);
    Account findByIdAndIsDeletedFalse(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :accountId AND a.isDeleted = false")
    Account findByIdForUpdate(@Param("accountId") UUID accountId);
}

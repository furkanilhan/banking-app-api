package com.furkan.banking.repository;

import com.furkan.banking.model.Account;
import com.furkan.banking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID>, JpaSpecificationExecutor<Account> {
    boolean existsByNameAndUser(String name, User user);
    Optional<Account> findByIdAndUser(UUID id, User user);
}

package com.furkan.banking.repository;

import com.furkan.banking.model.Account;
import com.furkan.banking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByNameAndUser(String name, User user);
    Optional<Account> findByIdAndUser(UUID id, User user);
}

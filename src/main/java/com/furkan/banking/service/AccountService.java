package com.furkan.banking.service;

import com.furkan.banking.dto.AccountDTO;
import com.furkan.banking.exception.CustomException;
import com.furkan.banking.mapper.AccountMapper;
import com.furkan.banking.model.Account;
import com.furkan.banking.model.User;
import com.furkan.banking.repository.AccountRepository;
import com.furkan.banking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;

    @Autowired
    public AccountService(AccountRepository accountRepository, UserRepository userRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.accountMapper = accountMapper;
    }

    public AccountDTO getAccountDetails(UUID accountId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "User not found");
        }

        Account account = accountRepository.findByIdAndUser(accountId, user)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Account not found"));

        return accountMapper.toAccountDTO(account);
    }

    public AccountDTO createAccount(String accountName, BigDecimal initialBalance) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (accountRepository.existsByNameAndUser(accountName, user)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "This account name already exists");
        }

        Account account = new Account();
        account.setName(accountName);
        account.setNumber(UUID.randomUUID().toString());
        account.setBalance(initialBalance);
        account.setUser(user);

        Account savedAccount = accountRepository.save(account);
        return accountMapper.toAccountDTO(savedAccount);
    }

    public AccountDTO updateAccount(UUID accountId, String newName, BigDecimal newBalance) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "User not found");
        }

        Account account = accountRepository.findByIdAndUser(accountId, user)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Account not found"));

        if (accountRepository.existsByNameAndUser(newName, user) && !account.getName().equals(newName)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "This account name already exists");
        }

        account.setName(newName);
        account.setBalance(newBalance);

        Account updatedAccount = accountRepository.save(account);

        return accountMapper.toAccountDTO(updatedAccount);
    }

    public void deleteAccount(UUID accountId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "User not found");
        }

        Account account = accountRepository.findByIdAndUser(accountId, user)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Account not found"));

        accountRepository.delete(account);
    }
}

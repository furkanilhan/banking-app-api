package com.furkan.banking.service;

import com.furkan.banking.dto.TransactionDTO;
import com.furkan.banking.dto.TransactionRequestDTO;
import com.furkan.banking.enums.TransactionStatus;
import com.furkan.banking.exception.CustomException;
import com.furkan.banking.mapper.TransactionMapper;
import com.furkan.banking.model.Account;
import com.furkan.banking.model.Transaction;
import com.furkan.banking.model.User;
import com.furkan.banking.repository.AccountRepository;
import com.furkan.banking.repository.TransactionRepository;
import com.furkan.banking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionDTO initiateTransfer(TransactionRequestDTO request) {
        Transaction transaction = new Transaction();
        Transaction savedTransaction;

        Account fromAccount = getAccountByNumberWithLock(request.getFromAccountNumber());
        Account toAccount = getAccountByNumberWithLock(request.getToAccountNumber());

        validateAccounts(request, fromAccount, toAccount);

        try {
            processTransfer(fromAccount, toAccount, request.getAmount());

            transaction = createTransaction(fromAccount, toAccount, request.getAmount());
            transaction.setStatus(TransactionStatus.SUCCESS);
            savedTransaction = transactionRepository.save(transaction);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);
            throw e;
        }
        return transactionMapper.toTransactionDTO(savedTransaction);
    }

    public Page<TransactionDTO> getTransactionHistory(UUID accountId, String username, Pageable pageable) {
        Account account = getAccountById(accountId);
        validateAccountOwner(account, username);
        Page<Transaction> transactionsPage = transactionRepository.findAllByAccountId(accountId, pageable);
        return transactionsPage.map(transactionMapper::toTransactionDTO);
    }

    private Account getAccountById(UUID accountId) {
        Account account = accountRepository.findByIdAndIsDeletedFalse(accountId);
        if (account == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Account not found");
        }
        return account;
    }

    private Account getAccountByNumberWithLock(String accountNumber) {
        Account account = accountRepository.findByNumberForUpdate(accountNumber);
        if (account == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Account not found");
        }
        return account;
    }

    private void validateAccounts(TransactionRequestDTO request, Account fromAccount, Account toAccount) {
        if (fromAccount.equals(toAccount)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Cannot transfer money to the same account");
        }
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Insufficient funds in the source account");
        }
    }

    private void processTransfer(Account fromAccount, Account toAccount, BigDecimal amount) {
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }

    private Transaction createTransaction(Account fromAccount, Account toAccount, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setFrom(fromAccount);
        transaction.setTo(toAccount);
        transaction.setAmount(amount);
        transaction.setTransactionDate(LocalDateTime.now());
        return transaction;
    }

    private void validateAccountOwner(Account account, String username) {
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (!account.getUser().equals(currentUser)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "You do not have permission to view this account's transactions");
        }
    }
}

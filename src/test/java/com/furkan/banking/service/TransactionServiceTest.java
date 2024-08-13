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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    private TransactionRequestDTO request;
    private Account fromAccount;
    private Account toAccount;
    private Transaction transaction;
    private User user;

    @BeforeEach
    public void setUp() {
        request = new TransactionRequestDTO();
        request.setFromAccountNumber("12345");
        request.setToAccountNumber("67890");
        request.setAmount(BigDecimal.valueOf(500));

        user = new User();
        user.setUsername("testuser");

        fromAccount = new Account();
        fromAccount.setNumber("12345");
        fromAccount.setBalance(BigDecimal.valueOf(1000));
        fromAccount.setUser(user);

        toAccount = new Account();
        toAccount.setNumber("67890");
        toAccount.setBalance(BigDecimal.valueOf(1000));
        toAccount.setUser(user);

        transaction = new Transaction();
        transaction.setFrom(fromAccount);
        transaction.setTo(toAccount);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.SUCCESS);
    }

    @Test
    public void testInitiateTransfer_Success() {
        when(accountRepository.findByNumberForUpdate(request.getFromAccountNumber())).thenReturn(fromAccount);
        when(accountRepository.findByNumberForUpdate(request.getToAccountNumber())).thenReturn(toAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toTransactionDTO(any(Transaction.class))).thenReturn(new TransactionDTO());

        TransactionDTO result = transactionService.initiateTransfer(request);

        assertNotNull(result);
        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
        verify(accountRepository, times(2)).save(any(Account.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void testInitiateTransfer_FailedDueToInsufficientFunds() {
        fromAccount.setBalance(BigDecimal.valueOf(100));

        when(accountRepository.findByNumberForUpdate(request.getFromAccountNumber())).thenReturn(fromAccount);
        when(accountRepository.findByNumberForUpdate(request.getToAccountNumber())).thenReturn(toAccount);

        CustomException exception = assertThrows(CustomException.class, () -> {
            transactionService.initiateTransfer(request);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Insufficient funds in the source account", exception.getMessage());
        verify(transactionRepository, times(0)).save(any(Transaction.class));  // Beklentiyi sıfır yapın
    }

    @Test
    public void testInitiateTransfer_FailedDueToSameAccountTransfer() {
        request.setToAccountNumber("12345");

        when(accountRepository.findByNumberForUpdate(request.getFromAccountNumber())).thenReturn(fromAccount);
        when(accountRepository.findByNumberForUpdate(request.getToAccountNumber())).thenReturn(fromAccount);

        CustomException exception = assertThrows(CustomException.class, () -> {
            transactionService.initiateTransfer(request);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Cannot transfer money to the same account", exception.getMessage());
    }

    @Test
    public void testGetTransactionHistory_Success() {
        UUID accountId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 5);
        Page<Transaction> transactionPage = new PageImpl<>(Arrays.asList(transaction));

        when(accountRepository.findByIdAndIsDeletedFalse(accountId)).thenReturn(fromAccount);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(transactionRepository.findAllByAccountId(accountId, pageable)).thenReturn(transactionPage);
        when(transactionMapper.toTransactionDTO(any(Transaction.class))).thenReturn(new TransactionDTO());

        Page<TransactionDTO> result = transactionService.getTransactionHistory(accountId, user.getUsername(), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(transactionRepository, times(1)).findAllByAccountId(accountId, pageable);
    }

    @Test
    public void testGetTransactionHistory_FailedDueToInvalidUser() {
        UUID accountId = UUID.randomUUID();

        when(accountRepository.findByIdAndIsDeletedFalse(accountId)).thenReturn(fromAccount);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(null);

        CustomException exception = assertThrows(CustomException.class, () -> {
            transactionService.getTransactionHistory(accountId, user.getUsername(), PageRequest.of(0, 5));
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testGetTransactionHistory_FailedDueToUnauthorizedAccess() {
        UUID accountId = UUID.randomUUID();
        User anotherUser = new User();
        anotherUser.setUsername("anotheruser");

        when(accountRepository.findByIdAndIsDeletedFalse(accountId)).thenReturn(fromAccount);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(anotherUser);

        CustomException exception = assertThrows(CustomException.class, () -> {
            transactionService.getTransactionHistory(accountId, user.getUsername(), PageRequest.of(0, 5));
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You do not have permission to view this account's transactions", exception.getMessage());
    }
}

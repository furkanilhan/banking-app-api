package com.furkan.banking.service;

import com.furkan.banking.dto.AccountDTO;
import com.furkan.banking.exception.CustomException;
import com.furkan.banking.mapper.AccountMapper;
import com.furkan.banking.model.Account;
import com.furkan.banking.model.User;
import com.furkan.banking.repository.AccountRepository;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AccountService accountService;

    private User user;
    private Account account;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUsername("testuser");

        account = new Account();
        account.setId(UUID.randomUUID());
        account.setName("Test Account");
        account.setBalance(BigDecimal.valueOf(1000));
        account.setUser(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
    }

    @Test
    public void testGetAccountDetails_ShouldReturnAccountDTO_WhenAccountExists() {
        when(accountRepository.findByIdAndUserAndIsDeletedFalse(account.getId(), user)).thenReturn(Optional.of(account));
        AccountDTO accountDTO = new AccountDTO();
        when(accountMapper.toAccountDTO(account)).thenReturn(accountDTO);

        AccountDTO result = accountService.getAccountDetails(account.getId());

        assertNotNull(result);
        verify(accountRepository, times(1)).findByIdAndUserAndIsDeletedFalse(account.getId(), user);
    }

    @Test
    public void testGetAccountDetails_ShouldThrowException_WhenAccountNotFound() {
        when(accountRepository.findByIdAndUserAndIsDeletedFalse(account.getId(), user)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> accountService.getAccountDetails(account.getId()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Account not found", exception.getMessage());
    }

    @Test
    public void testSearchAccounts_ShouldReturnPagedAccountDTOs() {
        Page<Account> accounts = new PageImpl<>(Arrays.asList(account));
        Pageable pageable = PageRequest.of(0, 5, Sort.by("name").ascending());
        Specification<Account> spec = mock(Specification.class);

        doReturn(accounts).when(accountRepository).findAll(any(Specification.class), eq(pageable));
        when(accountMapper.toAccountDTO(any(Account.class))).thenReturn(new AccountDTO());

        Page<AccountDTO> result = accountService.searchAccounts("Test Account", null, 0, 5, "name", "asc");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(accountRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }


    @Test
    public void testCreateAccount_ShouldCreateAndReturnAccountDTO() {
        when(accountRepository.existsByNameAndUserAndIsDeletedFalse(account.getName(), user)).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        AccountDTO accountDTO = new AccountDTO();
        when(accountMapper.toAccountDTO(account)).thenReturn(accountDTO);

        AccountDTO result = accountService.createAccount(account.getName(), BigDecimal.valueOf(1000));

        assertNotNull(result);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    public void testCreateAccount_ShouldThrowException_WhenAccountNameAlreadyExists() {
        when(accountRepository.existsByNameAndUserAndIsDeletedFalse(account.getName(), user)).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () -> accountService.createAccount(account.getName(), BigDecimal.valueOf(1000)));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("This account name already exists", exception.getMessage());
    }

    @Test
    public void testCreateAccount_ShouldThrowException_WhenInitialBalanceIsNegative() {
        CustomException exception = assertThrows(CustomException.class, () -> accountService.createAccount(account.getName(), BigDecimal.valueOf(-1000)));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Initial balance cannot be negative", exception.getMessage());
    }

    @Test
    public void testUpdateAccount_ShouldUpdateAndReturnAccountDTO() {
        when(accountRepository.findByIdAndUserAndIsDeletedFalse(account.getId(), user)).thenReturn(Optional.of(account));
        when(accountRepository.existsByNameAndUserAndIsDeletedFalse("New Account Name", user)).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        AccountDTO accountDTO = new AccountDTO();
        when(accountMapper.toAccountDTO(account)).thenReturn(accountDTO);

        AccountDTO result = accountService.updateAccount(account.getId(), "New Account Name", BigDecimal.valueOf(2000));

        assertNotNull(result);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    public void testUpdateAccount_ShouldThrowException_WhenAccountNameAlreadyExists() {
        when(accountRepository.findByIdAndUserAndIsDeletedFalse(account.getId(), user)).thenReturn(Optional.of(account));
        when(accountRepository.existsByNameAndUserAndIsDeletedFalse("Existing Account Name", user)).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () -> accountService.updateAccount(account.getId(), "Existing Account Name", BigDecimal.valueOf(2000)));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("This account name already exists", exception.getMessage());
    }

    @Test
    public void testUpdateAccount_ShouldThrowException_WhenNewBalanceIsNegative() {
        when(accountRepository.findByIdAndUserAndIsDeletedFalse(account.getId(), user)).thenReturn(Optional.of(account));

        CustomException exception = assertThrows(CustomException.class, () -> accountService.updateAccount(account.getId(), "New Account Name", BigDecimal.valueOf(-2000)));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Initial balance cannot be negative", exception.getMessage());
    }

    @Test
    public void testDeleteAccount_ShouldMarkAccountAsDeleted() {
        when(accountRepository.findByIdAndUserAndIsDeletedFalse(account.getId(), user)).thenReturn(Optional.of(account));

        accountService.deleteAccount(account.getId());

        assertTrue(account.isDeleted());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void testDeleteAccount_ShouldThrowException_WhenAccountNotFound() {
        when(accountRepository.findByIdAndUserAndIsDeletedFalse(account.getId(), user)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> accountService.deleteAccount(account.getId()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Account not found", exception.getMessage());
    }
}

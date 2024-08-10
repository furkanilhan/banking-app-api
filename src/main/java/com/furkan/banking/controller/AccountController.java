package com.furkan.banking.controller;

import com.furkan.banking.dto.AccountDTO;
import com.furkan.banking.payload.response.MessageResponse;
import com.furkan.banking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("${apiPrefix}/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountDetails(@PathVariable UUID id) {
        AccountDTO accountDetails = accountService.getAccountDetails(id);
        return ResponseEntity.ok(accountDetails);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<AccountDTO>> searchAccounts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String number,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Page<AccountDTO> accountPage = accountService.searchAccounts(name, number, page, size, sortField, sortDirection);
        return ResponseEntity.ok(accountPage);
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestParam String name,
                                                    @RequestParam BigDecimal initialBalance) {
        AccountDTO newAccount = accountService.createAccount(name, initialBalance);
        return ResponseEntity.ok(newAccount);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable UUID id,
                                                    @RequestParam String name,
                                                    @RequestParam BigDecimal balance) {
        AccountDTO updatedAccount = accountService.updateAccount(id, name, balance);
        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteAccount(@PathVariable UUID id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok(new MessageResponse("Account deleted successfully"));
    }
}

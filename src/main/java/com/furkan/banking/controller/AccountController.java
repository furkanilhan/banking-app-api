package com.furkan.banking.controller;

import com.furkan.banking.dto.AccountDTO;
import com.furkan.banking.payload.response.MessageResponse;
import com.furkan.banking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
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

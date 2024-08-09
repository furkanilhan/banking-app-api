package com.furkan.banking.controller;

import com.furkan.banking.dto.AccountDTO;
import com.furkan.banking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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
}

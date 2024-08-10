package com.furkan.banking.controller;

import com.furkan.banking.dto.TransactionDTO;
import com.furkan.banking.dto.TransactionRequestDTO;
import com.furkan.banking.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${apiPrefix}/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionDTO> initiateTransfer(@Valid @RequestBody TransactionRequestDTO request) {
        TransactionDTO transactionDTO = transactionService.initiateTransfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionDTO);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<Page<TransactionDTO>> getTransactionHistory(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionDTO> transactionHistory = transactionService.getTransactionHistory(accountId, username, pageable);

        return ResponseEntity.ok(transactionHistory);
    }
}

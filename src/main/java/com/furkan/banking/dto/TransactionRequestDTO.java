package com.furkan.banking.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequestDTO {
        private String fromAccountNumber;
        private String toAccountNumber;
        private BigDecimal amount;
}

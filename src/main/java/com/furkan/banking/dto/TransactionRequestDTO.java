package com.furkan.banking.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransactionRequestDTO {
        private UUID fromAccountId;
        private UUID toAccountId;
        private BigDecimal amount;
}

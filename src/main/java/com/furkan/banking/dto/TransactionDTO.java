package com.furkan.banking.dto;

import com.furkan.banking.enums.TransactionStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionDTO {
        private Long id;
        private UUID fromAccountId;
        private UUID toAccountId;
        private String fromAccountNumber;
        private String toAccountNumber;
        private BigDecimal amount;
        private LocalDateTime transactionDate;
        private TransactionStatus status;
}

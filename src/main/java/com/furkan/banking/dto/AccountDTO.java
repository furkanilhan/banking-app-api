package com.furkan.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private UUID id;
    private String number;
    private String name;
    private BigDecimal balance;
    private UUID userId;
    private boolean isDeleted;
}

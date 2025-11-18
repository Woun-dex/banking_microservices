package dev.bank.notification_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TransactionCompletedEvent {
    private String transactionRef;
    private String status;
    private UUID fromAccountId;
    private UUID toAccountId;
    private BigDecimal amount;
}

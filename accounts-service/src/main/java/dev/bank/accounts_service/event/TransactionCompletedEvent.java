package dev.bank.accounts_service.event;


import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransactionCompletedEvent {
    private String transactionRef;
    private UUID fromAccountId;
    private UUID toAccountId;
    private BigDecimal amount;
    private String status = "COMPLETED";

}

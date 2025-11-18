package dev.bank.accounts_service.event;

import lombok.Data;

@Data
public class TransactionFailedEvent {
    private String transactionRef;
    private String reason;
    private String status =  "FAILED";
}

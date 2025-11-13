package dev.bank.core.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionCompletedEvent {
    private UUID transactionID;
    String status;
    String message;
}

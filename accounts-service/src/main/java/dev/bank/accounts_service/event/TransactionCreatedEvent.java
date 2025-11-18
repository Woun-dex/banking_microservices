package dev.bank.accounts_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionCreatedEvent {

    private String transactionRef;
    private UUID fromAccountId ;
    private UUID toAccountId;
    private BigDecimal amount;

}


package dev.bank.accounts_service.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name= "ledger_entries")
@Getter
@Setter
public class LedgerEntry {

    @Id
    private UUID ledgerId = UUID.randomUUID();

    private UUID accountId;

    private String transactionRef;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private EntryType type;

    private LocalDateTime createdAt;
}

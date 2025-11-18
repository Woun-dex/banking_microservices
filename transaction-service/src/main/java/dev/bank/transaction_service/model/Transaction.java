package dev.bank.transaction_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="transactions", uniqueConstraints = {
        @UniqueConstraint(columnNames = "transactionRef")
})
@Getter
@Setter
public class Transaction {

    @Id
    private UUID trx =  UUID.randomUUID();

    @Column(nullable = false , unique = true)
    private String transactionRef ;

    private UUID fromAccountId ;

    private UUID toAccountId;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false , unique = true)
    private LocalDateTime createdAt;

    @Column(nullable = false , unique = true)
    private LocalDateTime updatedAt;


}

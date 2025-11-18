package dev.bank.accounts_service.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@Setter

public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID accountId ;

    private UUID userId;

    private BigDecimal balance;

    private String currency;

    @Version
    private Integer version;
}

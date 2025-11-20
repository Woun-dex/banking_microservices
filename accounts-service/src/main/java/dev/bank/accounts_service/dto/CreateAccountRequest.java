package dev.bank.accounts_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CreateAccountRequest {

    private UUID userId;
    private double initialBalance;
    private String currency;
}

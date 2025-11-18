package dev.bank.accounts_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToBalanceRequest {

    private UUID accountId;
    private BigDecimal amount;
}

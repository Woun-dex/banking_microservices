package dev.bank.accounts_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CreateAccountRequest {

    private Long userId;
    private String currency;
}

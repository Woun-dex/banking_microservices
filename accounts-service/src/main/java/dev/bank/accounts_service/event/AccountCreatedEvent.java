package dev.bank.accounts_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreatedEvent {
    UUID accountId;
    UUID userId;
    String currency ;
}

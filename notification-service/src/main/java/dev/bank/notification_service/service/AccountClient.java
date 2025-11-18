package dev.bank.notification_service.service;

import dev.bank.notification_service.dto.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "accounts-service")
public interface AccountClient {

    @GetMapping("/accounts/{id}")
    AccountResponse getAccountById(@PathVariable UUID id);
}

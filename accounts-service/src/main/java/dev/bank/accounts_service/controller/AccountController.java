package dev.bank.accounts_service.controller;

import dev.bank.accounts_service.dto.AccountResponse;
import dev.bank.accounts_service.dto.AddToBalanceRequest;
import dev.bank.accounts_service.dto.CreateAccountRequest;
import dev.bank.accounts_service.models.Account;
import dev.bank.accounts_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody CreateAccountRequest req) {
        return ResponseEntity.ok(service.createAccount(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getAccount(id));
    }

    @PostMapping("/add")
    public ResponseEntity<String> AddtoBalance(@RequestBody AddToBalanceRequest req) {
        service.AddToBalance(req.getAccountId() , req.getAmount());
        return ResponseEntity.ok("success ");
    }
}

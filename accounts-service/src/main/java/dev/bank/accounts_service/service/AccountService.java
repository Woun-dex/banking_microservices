package dev.bank.accounts_service.service;


import dev.bank.accounts_service.dto.AccountResponse;
import dev.bank.accounts_service.dto.CreateAccountRequest;
import dev.bank.accounts_service.event.AccountProducer;
import dev.bank.accounts_service.models.Account;
import dev.bank.accounts_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountProducer producer ;

    public AccountResponse createAccount(CreateAccountRequest request) {

        Account account = new Account();

        account.setUserId(request.getUserId());
        account.setCurrency(request.getCurrency());
        account.setBalance(BigDecimal.ZERO);
        account.setVersion(0);

        Account saved = accountRepository.save(account);

        producer.publishAccountCreated(saved);

        return new AccountResponse(
                saved.getId(),
                saved.getUserId(),
                saved.getBalance(),
                saved.getCurrency(),
                saved.getVersion()

        );
    }

    public AccountResponse getAccount(Long id) {
        Account acc = accountRepository.findById(id);
        if (acc == null) {
            throw new RuntimeException("Account not found");
        }

        return new AccountResponse(
                acc.getId(),
                acc.getUserId(),
                acc.getBalance(),
                acc.getCurrency(),
                acc.getVersion()
        );
    }
}

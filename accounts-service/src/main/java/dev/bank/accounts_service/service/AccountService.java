package dev.bank.accounts_service.service;


import dev.bank.accounts_service.dto.AccountResponse;
import dev.bank.accounts_service.dto.CreateAccountRequest;
import dev.bank.accounts_service.event.AccountProducer;
import dev.bank.accounts_service.event.TransactionCompletedEvent;
import dev.bank.accounts_service.event.TransactionFailedEvent;
import dev.bank.accounts_service.models.Account;
import dev.bank.accounts_service.models.EntryType;
import dev.bank.accounts_service.models.LedgerEntry;
import dev.bank.accounts_service.repository.AccountRepository;
import dev.bank.accounts_service.repository.LedgerEntryRepository;
import dev.bank.accounts_service.event.TransactionCreatedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerRepository;
    private final AccountProducer producer ;

    public AccountResponse createAccount(CreateAccountRequest request) {

        Account account = new Account();


        account.setUserId(request.getUserId());
        account.setCurrency(request.getCurrency());
        account.setBalance(BigDecimal.valueOf(request.getInitialBalance()));
        account.setVersion(0);

        Account saved = accountRepository.save(account);

        producer.publishAccountCreated(saved);

        return new AccountResponse(
                saved.getAccountId(),
                saved.getUserId(),
                saved.getBalance(),
                saved.getCurrency(),
                saved.getVersion()
        );
    }

    public AccountResponse getAccount(UUID id) {
        log.info("Getting account by ID: {}", id);
        Account acc = accountRepository.findByAccountId(id)
            .orElseThrow(()-> {
                log.error("Account not found with ID: {}", id);
                return new RuntimeException("Account not found with ID: " + id);
            });

        log.info("Found account: {} for user: {}", acc.getAccountId(), acc.getUserId());
        return new AccountResponse(
                acc.getAccountId(),
                acc.getUserId(),
                acc.getBalance(),
                acc.getCurrency(),
                acc.getVersion()
        );
    }

    @Transactional
    public void handleTransactionCreated(TransactionCreatedEvent event){

        if (ledgerRepository.existsByTransactionRefAndType(event.getTransactionRef(), EntryType.DEBIT)){
            return;
        }

        try {
            Account from = accountRepository.findByIdForUpdate(event.getFromAccountId()).orElseThrow(() -> new RuntimeException("Sender account not found"));

            Account to = accountRepository.findByIdForUpdate(event.getToAccountId()).orElseThrow(() -> new RuntimeException("Receiver account not found"));

            if (from.getBalance().compareTo(event.getAmount()) < 0) {
               publishFailure(event,"Insufficient funds");
                return;
            }

            from.setBalance(from.getBalance().subtract(event.getAmount()));
            to.setBalance(to.getBalance().add(event.getAmount()));

            accountRepository.save(from);
            accountRepository.save(to);

            writeLedger(event.getTransactionRef(), from.getUserId(), event.getAmount(), EntryType.DEBIT);
            writeLedger(event.getTransactionRef(), to.getUserId(), event.getAmount(), EntryType.CREDIT);

            TransactionCompletedEvent completed = new TransactionCompletedEvent();
            completed.setTransactionRef(event.getTransactionRef());
            completed.setFromAccountId(event.getFromAccountId());
            completed.setToAccountId(event.getToAccountId());
            completed.setAmount(event.getAmount());

            producer.publishTransactionCompleted(completed);


        }catch (Exception e) {
            publishFailure(event,e.getMessage());
        }
    }

    public void AddToBalance(UUID accountId, BigDecimal amount) {

        Account accountResponse = accountRepository.findByIdForUpdate(accountId).orElseThrow(() -> new RuntimeException("Account Not Found"));

        accountResponse.setBalance(accountResponse.getBalance().add(amount));

        accountRepository.save(accountResponse);


    }

    private void writeLedger(String transactionRef, UUID accountId, BigDecimal amount, EntryType type) {
        LedgerEntry entry = new LedgerEntry();
        entry.setAccountId(accountId);
        entry.setTransactionRef(transactionRef);
        entry.setAmount(amount);
        entry.setType(type);
        entry.setCreatedAt(LocalDateTime.now());
        ledgerRepository.save(entry);
    }

    private void publishFailure(TransactionCreatedEvent event, String reason) {
        TransactionFailedEvent failed = new TransactionFailedEvent();
        failed.setTransactionRef(event.getTransactionRef());
        failed.setReason(reason);
        producer.publishTransactionFailed(failed);
    }





}

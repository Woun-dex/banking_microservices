package dev.bank.transaction_service.service;


import dev.bank.transaction_service.core.TransactionProducer;
import dev.bank.transaction_service.dto.TransactionCreatedEvent;
import dev.bank.transaction_service.dto.TransactionResultEvent;
import dev.bank.transaction_service.dto.TransferRequest;
import dev.bank.transaction_service.model.Status;
import dev.bank.transaction_service.model.Transaction;
import dev.bank.transaction_service.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.TransactionException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final TransactionProducer producer ;

    @Transactional
    public Transaction initiateTransfer(TransferRequest request) {
        log.info("Starting transfer initiation for ref: {}", request.getTransactionRef());

        Optional<Transaction> transaction = repository.findByTransactionRef(request.getTransactionRef());
        if (transaction.isPresent()) {
            log.warn("Transaction already exists with ref: {}", request.getTransactionRef());
            return transaction.get();
        }

        if ( request.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            log.error("Invalid amount: {}", request.getAmount());
            throw new TransactionException("Amount must be greater than zero");
        }

        if ( request.getFromAccountId().equals(request.getToAccountId()) ){
            log.error("Same account IDs: from={}, to={}", request.getFromAccountId(), request.getToAccountId());
            throw new TransactionException("From account id must be different from to account id");
        }

        Transaction tx = new Transaction();

        tx.setTransactionRef(request.getTransactionRef());
        tx.setFromAccountId(request.getFromAccountId());
        tx.setToAccountId(request.getToAccountId());
        tx.setAmount(request.getAmount());
        tx.setStatus(Status.PENDING);
        tx.setCreatedAt(LocalDateTime.now());
        tx.setUpdatedAt(LocalDateTime.now());

        repository.save(tx);
        log.info("Transaction saved to database with ref: {}", request.getTransactionRef());
        
        log.info("About to publish Kafka event for transaction ref: {}", request.getTransactionRef());
        try {
            producer.publishTransactionCreated(new TransactionCreatedEvent(
                    request.getTransactionRef(),
                    request.getFromAccountId(),
                    request.getToAccountId(),
                    request.getAmount()
            ));
            log.info("Kafka event publishing initiated for transaction ref: {}", request.getTransactionRef());
        } catch (Exception e) {
            log.error("CRITICAL: Failed to publish Kafka event for transaction ref: {}", request.getTransactionRef(), e);
            throw e;
        }
        
        return tx;
    }

    @Transactional
    public void updateTransactionStatus(TransactionResultEvent event){
        log.info("Updating transaction status for ref: {} with status: {}", event.getTransactionRef(), event.getStatus());

        Transaction tx = repository.findByTransactionRef(event.getTransactionRef())
                .orElseThrow(() -> new TransactionException("Transaction not found"));

        Status newStatus;
        if ("COMPLETED".equalsIgnoreCase(event.getStatus()) || "ACCEPTED".equalsIgnoreCase(event.getStatus())) {
            newStatus = Status.ACCEPTED;
        } else if ("FAILED".equalsIgnoreCase(event.getStatus()) || "REJECTED".equalsIgnoreCase(event.getStatus())) {
            newStatus = Status.REJECTED;
        } else {
            log.warn("Unknown status '{}' for transaction ref: {}. Setting to REJECTED.", event.getStatus(), event.getTransactionRef());
            newStatus = Status.REJECTED;
        }

        tx.setStatus(newStatus);
        tx.setUpdatedAt(LocalDateTime.now());

        repository.save(tx);
        log.info("Transaction status updated successfully for ref: {} to status: {}", event.getTransactionRef(), newStatus);
    }
}

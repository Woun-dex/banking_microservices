package dev.bank.transaction_service.core;

import dev.bank.transaction_service.dto.TransactionResultEvent;
import dev.bank.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionResultConsumer {

    private final TransactionService service;

    @KafkaListener(topics = "transactions.completed", groupId = "transaction-service")
    public void consumeCompleted(TransactionResultEvent event){
        log.info("Received TransactionCompleted event from Kafka: {}", event.getTransactionRef());
        try {
            service.updateTransactionStatus(event);
            log.info("Successfully processed TransactionCompleted event: {}", event.getTransactionRef());
        } catch (Exception e) {
            log.error("Failed to process TransactionCompleted event: {}", event.getTransactionRef(), e);
            throw e;
        }
    }

    @KafkaListener(topics = "transactions.failed", groupId = "transaction-service")
    public void consumeFailed(TransactionResultEvent event){
        log.info("Received TransactionFailed event from Kafka: {}", event.getTransactionRef());
        try {
            service.updateTransactionStatus(event);
            log.info("Successfully processed TransactionFailed event: {}", event.getTransactionRef());
        } catch (Exception e) {
            log.error("Failed to process TransactionFailed event: {}", event.getTransactionRef(), e);
            throw e;
        }
    }
}

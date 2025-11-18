package dev.bank.accounts_service.event;

import dev.bank.accounts_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionCreatedConsumer {

    private final AccountService accountService;

    @KafkaListener(topics = "transaction.created" , groupId = "account-service")
    public void consume(TransactionCreatedEvent event) {
        log.info("Received TransactionCreatedEvent from Kafka topic: {}", event.getTransactionRef());
        accountService.handleTransactionCreated(event);
    }
}

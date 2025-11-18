package dev.bank.notification_service.service;

import dev.bank.notification_service.dto.TransactionCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionCompletedListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "transactions.completed", groupId = "notification-service")
    public void listen(TransactionCompletedEvent transactionCompletedEvent) {
        log.info("Received TransactionCompletedEvent for transaction: {}", transactionCompletedEvent.getTransactionRef());
        try {
            notificationService.handle(transactionCompletedEvent);
            log.info("Successfully processed TransactionCompletedEvent: {}", transactionCompletedEvent.getTransactionRef());
        } catch (Exception e) {
            log.error("Failed to process TransactionCompletedEvent: {}", transactionCompletedEvent.getTransactionRef(), e);
            throw e;
        }
    }

}

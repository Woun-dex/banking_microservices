package dev.bank.transaction_service.core;

import dev.bank.transaction_service.dto.TransactionCreatedEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionProducer {

    private final KafkaTemplate<String , Object> kafkaTemplate;

    @PostConstruct
    public void init() {
        log.info("TransactionProducer initialized with KafkaTemplate: {}", kafkaTemplate != null ? "SUCCESS" : "FAILED");
        if (kafkaTemplate != null) {
            log.info("KafkaTemplate default topic: {}", kafkaTemplate.getDefaultTopic());
        }
    }

    public void publishTransactionCreated(TransactionCreatedEvent event) {
        log.info("Publishing TransactionCreatedEvent to Kafka topic 'transaction.created': {}", event.getTransactionRef());
        
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send("transaction.created", event.getTransactionRef(), event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully sent message to topic 'transaction.created' with offset: {} for transaction: {}", 
                    result.getRecordMetadata().offset(), event.getTransactionRef());
            } else {
                log.error("Failed to send message to Kafka topic 'transaction.created' for transaction: {}", 
                    event.getTransactionRef(), ex);
            }
        });
    }
}

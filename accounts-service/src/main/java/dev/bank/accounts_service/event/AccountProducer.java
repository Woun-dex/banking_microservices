package dev.bank.accounts_service.event;

import dev.bank.accounts_service.models.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountProducer {

    private final KafkaTemplate<String , Object> kafkaTemplate;

    public void publishAccountCreated(Account account){

        var event = new AccountCreatedEvent(
        account.getAccountId(),
        account.getUserId(),
        account.getCurrency()
        );

        kafkaTemplate.send("account_created", event);
    }

    public void publishTransactionCompleted(TransactionCompletedEvent event){
        log.info("Publishing TransactionCompleted event for ref: {} with status: {}", event.getTransactionRef(), event.getStatus());
        kafkaTemplate.send("transactions.completed", event.getTransactionRef() , event)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully sent TransactionCompleted to Kafka for ref: {}", event.getTransactionRef());
                } else {
                    log.error("Failed to send TransactionCompleted to Kafka for ref: {}", event.getTransactionRef(), ex);
                }
            });
    }

    public void publishTransactionFailed(TransactionFailedEvent event){
        log.info("Publishing TransactionFailed event for ref: {} with reason: {}", event.getTransactionRef(), event.getReason());
        kafkaTemplate.send("transactions.failed", event.getTransactionRef() , event)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully sent TransactionFailed to Kafka for ref: {}", event.getTransactionRef());
                } else {
                    log.error("Failed to send TransactionFailed to Kafka for ref: {}", event.getTransactionRef(), ex);
                }
            });
    }


}

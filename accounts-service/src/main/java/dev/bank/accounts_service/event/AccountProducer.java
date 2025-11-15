package dev.bank.accounts_service.event;

import dev.bank.accounts_service.models.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AccountProducer {

    private final KafkaTemplate<String , Object> kafkaTemplate;

    public AccountProducer(KafkaTemplate<String,Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate ;
    }
    public void publishAccountCreated(Account account){

        var event = new AccountCreatedEvent(
        account.getId(),
        account.getUserId(),
        account.getCurrency()
        );

        kafkaTemplate.send("account_created", event);
    }
}

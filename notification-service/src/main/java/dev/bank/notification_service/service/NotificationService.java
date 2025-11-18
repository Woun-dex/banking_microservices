package dev.bank.notification_service.service;


import dev.bank.notification_service.dto.AccountResponse;
import dev.bank.notification_service.dto.TransactionCompletedEvent;
import dev.bank.notification_service.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final UserClient userClient;
    private final AccountClient accountClient ;

    public void handle(TransactionCompletedEvent event){
        log.info("Handling TransactionCompletedEvent for ref: {}, fromAccountId: {}", 
            event.getTransactionRef(), event.getFromAccountId());
        
        // First get the account to retrieve the userId
        AccountResponse account = accountClient.getAccountById(event.getFromAccountId());
        log.info("Retrieved account: {} for user: {}", account.getAccountId(), account.getUserId());

        // Then get the user details
        UserResponse user = userClient.getUserById(account.getUserId());
        log.info("Retrieved user: {} ({})", user.getUsername(), user.getId());

        String message = String.format(
                "📢 Transaction Completed for %s | Amount: %s MAD | Status: %s | Ref: %s",
                user.getUsername(),
                event.getAmount(),
                event.getStatus(),
                event.getTransactionRef()
        );

        log.info(message);
    }
}

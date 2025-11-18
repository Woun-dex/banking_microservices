package dev.bank.transaction_service.controller;


import dev.bank.transaction_service.dto.TransferRequest;
import dev.bank.transaction_service.dto.TransferResponse;
import dev.bank.transaction_service.model.Transaction;
import dev.bank.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service ;


    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@RequestBody TransferRequest request){
        try {
            log.info("Received transfer request for ref: {}", request.getTransactionRef());
            
            Transaction transaction = service.initiateTransfer(request);
            
            TransferResponse response = new TransferResponse(
                transaction.getTransactionRef(),
                transaction.getStatus().toString(),
                "Transaction initiated successfully and is being processed"
            );
            
            log.info("Transfer initiated successfully for ref: {} with status: {}", 
                transaction.getTransactionRef(), transaction.getStatus());
            
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
            
        } catch (Exception e) {
            log.error("Failed to initiate transfer for ref: {}", request.getTransactionRef(), e);
            
            TransferResponse errorResponse = new TransferResponse(
                request.getTransactionRef(),
                "FAILED",
                "Transaction failed: " + e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

}

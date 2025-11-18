package dev.bank.accounts_service.repository;

import dev.bank.accounts_service.models.EntryType;
import dev.bank.accounts_service.models.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {

    boolean existsByTransactionRefAndType(String transactionRef, EntryType type);
}

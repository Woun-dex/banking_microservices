package dev.bank.accounts_service.repository;

import dev.bank.accounts_service.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Integer> {
    Account findById(Long id);
}

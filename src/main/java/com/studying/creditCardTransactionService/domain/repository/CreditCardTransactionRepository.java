package com.studying.creditCardTransactionService.domain.repository;

import com.studying.creditCardTransactionService.domain.model.CreditCardTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CreditCardTransactionRepository extends JpaRepository<CreditCardTransaction, UUID> {
}

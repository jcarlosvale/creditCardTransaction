package com.studying.creditCardTransactionService.domain.repository;

import com.studying.creditCardTransactionService.domain.model.BalanceOfCategory;
import com.studying.creditCardTransactionService.domain.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BalanceOfCategoryRepository extends JpaRepository<BalanceOfCategory, UUID> {

    Optional<BalanceOfCategory> findBalanceOfCategoryByAccountIdAndCategory(String accountId, Category category);
}

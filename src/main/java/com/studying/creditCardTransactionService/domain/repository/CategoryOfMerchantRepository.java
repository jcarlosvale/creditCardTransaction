package com.studying.creditCardTransactionService.domain.repository;

import com.studying.creditCardTransactionService.domain.model.CategoryOfMerchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryOfMerchantRepository extends JpaRepository<CategoryOfMerchant, UUID> {
    Optional<CategoryOfMerchant> findCategoryOfMerchantByMerchant(String merchant);
}

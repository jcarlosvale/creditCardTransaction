package com.studying.creditCardTransactionService.domain.service;

import com.studying.creditCardTransactionService.domain.model.CategoryOfMerchant;

import java.util.Optional;

public interface CategoryOfMerchantService {
    Optional<CategoryOfMerchant> findByMerchant(String merchant);
}

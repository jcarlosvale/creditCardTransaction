package com.studying.creditCardTransactionService.domain.service;

import com.studying.creditCardTransactionService.domain.model.BalanceOfCategory;
import com.studying.creditCardTransactionService.domain.model.Category;

import java.math.BigDecimal;
import java.util.Optional;

public interface BalanceOfCategoryService {
    Optional<BalanceOfCategory> findByAccountAndMCC(String accountId, String mcc);
    boolean debit(BalanceOfCategory balanceOfCategory, BigDecimal amount);

    Optional<BalanceOfCategory> findByAccountAndCategory(String accountId, Category category);

    Optional<BalanceOfCategory> findByMerchant(String accountId, String merchant);
}

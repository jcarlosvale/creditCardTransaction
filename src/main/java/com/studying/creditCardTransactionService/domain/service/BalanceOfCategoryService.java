package com.studying.creditCardTransactionService.domain.service;

import com.studying.creditCardTransactionService.domain.model.BalanceOfCategory;

import java.math.BigDecimal;
import java.util.Optional;

public interface BalanceOfCategoryService {
    Optional<BalanceOfCategory> findByAccountAndMCC(String accountId, String mcc);
    boolean debit(BalanceOfCategory balanceOfCategory, BigDecimal amount);
}

package com.studying.creditCardTransactionService.domain.service.impl;

import com.studying.creditCardTransactionService.domain.model.BalanceOfCategory;
import com.studying.creditCardTransactionService.domain.model.Category;
import com.studying.creditCardTransactionService.domain.repository.BalanceOfCategoryRepository;
import com.studying.creditCardTransactionService.domain.service.BalanceOfCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BalanceOfCategoryServiceImpl implements BalanceOfCategoryService {

    private final BalanceOfCategoryRepository balanceOfCategoryRepository;

    @Override
    public Optional<BalanceOfCategory> findByAccountAndMCC(final String accountId, final String mcc) {
        Objects.requireNonNull(accountId, "acocuntId must not be null");
        Objects.requireNonNull(accountId, "mcc must not be null");
        final var category = Category.fromMCC(mcc);
        return balanceOfCategoryRepository.findBalanceOfCategoryByAccountIdAndCategory(accountId, category);
    }

    @Override
    public boolean debit(final BalanceOfCategory balanceOfCategory, final BigDecimal amount) {
        Objects.requireNonNull(balanceOfCategory, "balanceOfCategory must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        if (balanceOfCategory.hasBalance(amount)) {
            final var updatedAmount = balanceOfCategory.getAmount().subtract(amount);
            balanceOfCategory.setAmount(updatedAmount);
            balanceOfCategoryRepository.save(balanceOfCategory);
            return true;
        }
        return false;
    }
}

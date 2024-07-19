package com.studying.creditCardTransactionService.domain.service.impl;

import com.studying.creditCardTransactionService.domain.model.BalanceOfCategory;
import com.studying.creditCardTransactionService.domain.model.Category;
import com.studying.creditCardTransactionService.domain.repository.BalanceOfCategoryRepository;
import com.studying.creditCardTransactionService.domain.service.BalanceOfCategoryService;
import com.studying.creditCardTransactionService.domain.service.CategoryOfMerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BalanceOfCategoryServiceImpl implements BalanceOfCategoryService {

    private final BalanceOfCategoryRepository balanceOfCategoryRepository;
    private final CategoryOfMerchantService categoryOfMerchantService;

    @Override
    public Optional<BalanceOfCategory> findByAccountAndMCC(final String accountId, final String mcc) {
        Objects.requireNonNull(accountId, "acocuntId must not be null");
        Objects.requireNonNull(accountId, "mcc must not be null");
        final var category = Category.fromMCC(mcc);
        return findByAccountAndCategory(accountId, category);
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

    @Override
    public Optional<BalanceOfCategory> findByAccountAndCategory(final String accountId, final Category category) {
        Objects.requireNonNull(accountId, "acocuntId must not be null");
        Objects.requireNonNull(accountId, "category must not be null");
        return balanceOfCategoryRepository.findBalanceOfCategoryByAccountIdAndCategory(accountId, category);
    }

    @Override
    public Optional<BalanceOfCategory> findByMerchant(final String accountId, final String merchant) {

        Objects.requireNonNull(accountId, "acocuntId must not be null");
        Objects.requireNonNull(merchant, "merchant must not be null");

        final var categoryOptional = categoryOfMerchantService.findByMerchant(merchant);

        if(categoryOptional.isEmpty()) {
             return Optional.empty();
        } else {
            return findByAccountAndCategory(accountId, categoryOptional.get().getCategory());
        }
    }
}

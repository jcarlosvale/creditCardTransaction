package com.studying.creditCardTransactionService.domain.dto;

import com.studying.creditCardTransactionService.domain.model.BalanceOfCategory;
import com.studying.creditCardTransactionService.domain.model.CreditCardTransaction;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Builder
public record CreditTransactionDto(UUID id,
                                   String accountId,
                                   BigDecimal amount,
                                   String merchant,
                                   String mcc) {
    public CreditCardTransaction toCreditCardTransaction(BalanceOfCategory balanceOfCategory) {
        Objects.requireNonNull(balanceOfCategory, "balanceOfCategory must not be null");
        return CreditCardTransaction.builder()
                .id(id)
                .accountId(accountId)
                .amount(amount)
                .merchant(merchant)
                .balance(balanceOfCategory)
                .build();
    }
}

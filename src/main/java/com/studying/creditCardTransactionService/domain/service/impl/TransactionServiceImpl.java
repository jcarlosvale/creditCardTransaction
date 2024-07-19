package com.studying.creditCardTransactionService.domain.service.impl;

import com.studying.creditCardTransactionService.domain.dto.CreditTransactionDto;
import com.studying.creditCardTransactionService.domain.model.BalanceOfCategory;
import com.studying.creditCardTransactionService.domain.model.Category;
import com.studying.creditCardTransactionService.domain.model.TransactionStatus;
import com.studying.creditCardTransactionService.domain.repository.CreditCardTransactionRepository;
import com.studying.creditCardTransactionService.domain.service.BalanceOfCategoryService;
import com.studying.creditCardTransactionService.domain.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final BalanceOfCategoryService balanceOfCategoryService;
    private final CreditCardTransactionRepository repository;

    @Override
    public TransactionStatus registerTransaction(final CreditTransactionDto transactionDto) {
        Objects.requireNonNull(transactionDto, "transactionDto must not be null");
        if (isNewTransaction(transactionDto)) {
            return processTransaction(transactionDto);
        } else {
            log.error("It is not a new transaction [id=%s]".formatted(transactionDto));
            return TransactionStatus.ERRO;
        }
    }

    private boolean isNewTransaction(final CreditTransactionDto transactionDto) {
        return !repository.existsById(transactionDto.id());
    }

    private TransactionStatus processTransaction(final CreditTransactionDto transactionDto) {
        final var accountId = transactionDto.accountId();
        final var mcc = transactionDto.mcc();
        final var amount = transactionDto.amount();
        final var balanceOfCategoryOptional = findBalanceOfCategory(transactionDto);
        final var balanceOfCategoryFallbackOptional = balanceOfCategoryService.findByAccountAndCategory(accountId, Category.CASH);
        if (balanceOfCategoryOptional.isEmpty() && balanceOfCategoryFallbackOptional.isEmpty()) {
            log.info("balanceOfCategory not found [accountId=%s, mcc=%s]".formatted(accountId, mcc));
            return TransactionStatus.ERRO;
        } else {
            final var balanceOfCategory = balanceOfCategoryOptional.get();
            final var balanceOfCategoryFallback = balanceOfCategoryFallbackOptional.get();
            return persistTransactionAndNewBalance(transactionDto, balanceOfCategory, balanceOfCategoryFallback, amount);
        }
    }

    private Optional<BalanceOfCategory> findBalanceOfCategory(final CreditTransactionDto transactionDto) {

        final var merchant = transactionDto.merchant();
        final var accountId = transactionDto.accountId();
        final var mcc = transactionDto.mcc();

        final var balanceOfCategoryByMerchantOptional = balanceOfCategoryService.findByMerchant(accountId, merchant);

        if(balanceOfCategoryByMerchantOptional.isEmpty()) {
            return balanceOfCategoryService.findByAccountAndMCC(accountId, mcc);
        } else {
            return balanceOfCategoryByMerchantOptional;
        }
    }

    @Transactional
    protected TransactionStatus persistTransactionAndNewBalance(
            final CreditTransactionDto transactionDto, final BalanceOfCategory balanceOfCategory,
            final BalanceOfCategory balanceOfCategoryFallback, final BigDecimal amount) {
        try {
            if (balanceOfCategoryService.debit(balanceOfCategory, amount) ||
                    balanceOfCategoryService.debit(balanceOfCategoryFallback, amount)) {

                repository.save(transactionDto.toCreditCardTransaction(balanceOfCategory));
                return TransactionStatus.APROVADA;

            } else {
                return TransactionStatus.REJEITADA;
            }
        } catch (final OptimisticLockingFailureException e) {
            log.error("Update failed due to concurrent modification", e);
            return TransactionStatus.ERRO;
        }
    }
}

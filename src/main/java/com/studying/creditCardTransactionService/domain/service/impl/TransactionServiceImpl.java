package com.studying.creditCardTransactionService.domain.service.impl;

import com.studying.creditCardTransactionService.domain.dto.CreditTransactionDto;
import com.studying.creditCardTransactionService.domain.model.BalanceOfCategory;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final BalanceOfCategoryService balanceOfCategoryService;
    private final CreditCardTransactionRepository repository;

    @Override
    public TransactionStatus registerTransaction(final CreditTransactionDto transactionDto) {
        Objects.requireNonNull(transactionDto, "transactionDto must not be null");
        if(isNewTransaction(transactionDto)) {
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
        final var balanceOfCategoryOptional = balanceOfCategoryService.findByAccountAndMCC(accountId, mcc);
        if (balanceOfCategoryOptional.isEmpty()) {
            log.info("balanceOfCategory not found [accountId=%s, mcc=%s]".formatted(accountId, mcc));
            return TransactionStatus.ERRO;
        } else {
            final var balanceOfCategory = balanceOfCategoryOptional.get();
            return persistTransactionAndNewBalance(transactionDto, balanceOfCategory, amount);
        }
    }

    @Transactional
    protected TransactionStatus persistTransactionAndNewBalance(final CreditTransactionDto transactionDto,
                                                                final BalanceOfCategory balanceOfCategory,
                                                                final BigDecimal amount) {
        try {
            if (balanceOfCategoryService.debit(balanceOfCategory, amount)) {
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

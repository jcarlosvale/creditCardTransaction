package com.studying.creditCardTransactionService.domain.service.impl;

import com.studying.creditCardTransactionService.domain.dto.CreditTransactionDto;
import com.studying.creditCardTransactionService.domain.model.BalanceOfCategory;
import com.studying.creditCardTransactionService.domain.model.CreditCardTransaction;
import com.studying.creditCardTransactionService.domain.model.TransactionStatus;
import com.studying.creditCardTransactionService.domain.repository.CreditCardTransactionRepository;
import com.studying.creditCardTransactionService.domain.service.BalanceOfCategoryService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private BalanceOfCategoryService balanceOfCategoryService;

    @Mock
    private CreditCardTransactionRepository repository;

    @InjectMocks
    private TransactionServiceImpl service;

    @Test
    void registerTransactionApprovedTest() {
        // given
        final var isDebitAllowed = true;
        final var expected = TransactionStatus.APROVADA;
        final var transactionDto =
                CreditTransactionDto.builder()
                        .id(UUID.randomUUID())
                        .amount(BigDecimal.valueOf(Math.random()))
                        .accountId("some account id")
                        .mcc("some mcc code")
                        .build();
        final BalanceOfCategory balanceOfCategory =
                BalanceOfCategory.builder()
                        .id(UUID.randomUUID())
                        .build();
        final var expectedCreditCardTransaction = transactionDto.toCreditCardTransaction(balanceOfCategory);
        final Optional<BalanceOfCategory> balanceOfCategoryOptional = Optional.of(balanceOfCategory);

        given(repository.existsById(transactionDto.id()))
                .willReturn(false);
        given(balanceOfCategoryService
                      .findByAccountAndMCC(transactionDto.accountId(), transactionDto.mcc()))
                .willReturn(balanceOfCategoryOptional);
        given(balanceOfCategoryService.debit(balanceOfCategory,transactionDto.amount()))
                .willReturn(isDebitAllowed);

        // when
        final var actual = service.registerTransaction(transactionDto);

        // then
        Assertions.assertThat(actual).isEqualTo(expected);
        verify(repository, times(1)).save(expectedCreditCardTransaction);
    }

    @Test
    void registerTransactionRejectedTest() {
        // given
        final var isDebitAllowed = false;
        final var expected = TransactionStatus.REJEITADA;
        final var transactionDto =
                CreditTransactionDto.builder()
                        .id(UUID.randomUUID())
                        .amount(BigDecimal.valueOf(Math.random()))
                        .accountId("some account id")
                        .mcc("some mcc code")
                        .build();
        final BalanceOfCategory balanceOfCategory =
                BalanceOfCategory.builder()
                        .id(UUID.randomUUID())
                        .build();
        final Optional<BalanceOfCategory> balanceOfCategoryOptional = Optional.of(balanceOfCategory);

        given(repository.existsById(transactionDto.id()))
                .willReturn(false);
        given(balanceOfCategoryService
                      .findByAccountAndMCC(transactionDto.accountId(), transactionDto.mcc()))
                .willReturn(balanceOfCategoryOptional);
        given(balanceOfCategoryService.debit(balanceOfCategory,transactionDto.amount()))
                .willReturn(isDebitAllowed);

        // when
        final var actual = service.registerTransaction(transactionDto);

        // then
        Assertions.assertThat(actual).isEqualTo(expected);
        verify(repository, never()).save(any(CreditCardTransaction.class));
    }

    @Test
    void registerTransactionNotUniqueErrorTest() {
        // given
        final var expected = TransactionStatus.ERRO;
        final var transactionDto =
                CreditTransactionDto.builder()
                        .id(UUID.randomUUID())
                        .amount(BigDecimal.valueOf(Math.random()))
                        .accountId("some account id")
                        .mcc("some mcc code")
                        .build();
        given(repository.existsById(transactionDto.id()))
                .willReturn(true);

        // when
        final var actual = service.registerTransaction(transactionDto);

        // then
        Assertions.assertThat(actual).isEqualTo(expected);
        verify(repository, never()).save(any(CreditCardTransaction.class));
    }

    @Test
    void registerTransactionWithoutCategoryErrorTest() {
        // given
        final var expected = TransactionStatus.ERRO;
        final var transactionDto =
                CreditTransactionDto.builder()
                        .id(UUID.randomUUID())
                        .amount(BigDecimal.valueOf(Math.random()))
                        .accountId("some account id")
                        .mcc("some mcc code")
                        .build();
        final Optional<BalanceOfCategory> balanceOfCategoryOptional = Optional.empty();

        given(repository.existsById(transactionDto.id()))
                .willReturn(false);
        given(balanceOfCategoryService
                      .findByAccountAndMCC(transactionDto.accountId(), transactionDto.mcc()))
                .willReturn(balanceOfCategoryOptional);

        // when
        final var actual = service.registerTransaction(transactionDto);

        // then
        Assertions.assertThat(actual).isEqualTo(expected);
        verify(repository, never()).save(any(CreditCardTransaction.class));
    }

    @Test
    void registerTransactionExceptionErrorTest() {
        // given
        final var isDebitAllowed = true;
        final var expected = TransactionStatus.ERRO;
        final var transactionDto =
                CreditTransactionDto.builder()
                        .id(UUID.randomUUID())
                        .amount(BigDecimal.valueOf(Math.random()))
                        .accountId("some account id")
                        .mcc("some mcc code")
                        .build();
        final BalanceOfCategory balanceOfCategory =
                BalanceOfCategory.builder()
                        .id(UUID.randomUUID())
                        .build();
        final var expectedCreditCardTransaction = transactionDto.toCreditCardTransaction(balanceOfCategory);
        final Optional<BalanceOfCategory> balanceOfCategoryOptional = Optional.of(balanceOfCategory);

        given(repository.existsById(transactionDto.id()))
                .willReturn(false);
        given(balanceOfCategoryService
                      .findByAccountAndMCC(transactionDto.accountId(), transactionDto.mcc()))
                .willReturn(balanceOfCategoryOptional);
        given(balanceOfCategoryService.debit(balanceOfCategory,transactionDto.amount()))
                .willReturn(isDebitAllowed);
        given(repository.save(expectedCreditCardTransaction)).willThrow(new OptimisticLockingFailureException(""));

        // when
        final var actual = service.registerTransaction(transactionDto);

        // then
        Assertions.assertThat(actual).isEqualTo(expected);
        verify(repository, times(1)).save(expectedCreditCardTransaction);
    }
}
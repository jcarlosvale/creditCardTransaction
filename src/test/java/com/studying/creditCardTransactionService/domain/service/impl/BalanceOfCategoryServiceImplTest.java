package com.studying.creditCardTransactionService.domain.service.impl;

import com.studying.creditCardTransactionService.domain.model.BalanceOfCategory;
import com.studying.creditCardTransactionService.domain.model.Category;
import com.studying.creditCardTransactionService.domain.repository.BalanceOfCategoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BalanceOfCategoryServiceImplTest {

    @Mock
    private BalanceOfCategoryRepository repository;

    @Mock
    private BalanceOfCategory balanceOfCategory;

    @InjectMocks
    private BalanceOfCategoryServiceImpl service;

    @Test
    void findByAccountAndMCCTest() {
        // given
        final var mcc = "5811";
        final var accountId = "some account id";
        final var category = Category.MEAL;
        final var expected = Optional.of(balanceOfCategory);

        given(repository.findBalanceOfCategoryByAccountIdAndCategory(accountId, category))
                .willReturn(expected);
        // when
        final var actual = service.findByAccountAndMCC(accountId, mcc);

        // then
        Assertions.assertThat(actual).isEqualTo(expected);
    }


    @Test
    void debitNotAllowedTest() {
        // given
        final var amount = new BigDecimal("1.03");
        final var balance = new BigDecimal("1.02");
        final var balanceOfCategory = BalanceOfCategory.builder()
                .id(UUID.randomUUID())
                .amount(balance)
                .build();

        // when
        final var actual = service.debit(balanceOfCategory, amount);

        // then
        Assertions.assertThat(actual).isFalse();
        verify(repository, never()).save(any(BalanceOfCategory.class));
    }

    @ParameterizedTest
    @MethodSource("debitArguments")
    void debitAllowedTest(BigDecimal amount, BigDecimal balance, BigDecimal updatedAmount) {
        // given
        final var balanceOfCategory = BalanceOfCategory.builder()
                .id(UUID.randomUUID())
                .amount(balance)
                .build();
        final var updatedBalanceOfCategory = BalanceOfCategory.builder()
                .id(balanceOfCategory.getId())
                .amount(updatedAmount)
                .build();


        // when
        final var actual = service.debit(balanceOfCategory, amount);

        // then
        Assertions.assertThat(actual).isTrue();
        verify(repository, times(1)).save(updatedBalanceOfCategory);
    }

    public static Stream<Arguments> debitArguments() {
        return Stream.of(
            Arguments.of(new BigDecimal("10.01"), new BigDecimal("10.02"), new BigDecimal("0.01")),
            Arguments.of(new BigDecimal("0.01"), new BigDecimal("0.01"), new BigDecimal("0.00"))
                        );
    }

}
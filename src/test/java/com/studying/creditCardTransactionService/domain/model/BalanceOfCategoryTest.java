package com.studying.creditCardTransactionService.domain.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

class BalanceOfCategoryTest {

    @ParameterizedTest
    @MethodSource("hasBalanceArgument")
    void hasBalanceTest(final BigDecimal balance, final BigDecimal amount, final boolean expected) {
        // given
        final var balanceOfCategory =
                BalanceOfCategory.builder()
                        .amount(balance)
                        .build();
        // when
        final var actual = balanceOfCategory.hasBalance(amount);

        // then
        Assertions.assertThat(actual).isEqualTo(expected);

    }

    public static Stream<Arguments> hasBalanceArgument() {
        return Stream.of(
                Arguments.of(new BigDecimal("0.99"), new BigDecimal("0.98"), true),
                Arguments.of(new BigDecimal("0.01"), new BigDecimal("0.01"), true),
                Arguments.of(new BigDecimal("1.01"), new BigDecimal("1.02"), false)
                        );
    }

}
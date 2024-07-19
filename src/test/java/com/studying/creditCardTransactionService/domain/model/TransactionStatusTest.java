package com.studying.creditCardTransactionService.domain.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class TransactionStatusTest {

    public static Stream<Arguments> transactionStatusArgument() {
        return Stream.of(
                Arguments.of(TransactionStatus.APROVADA, "00"),
                Arguments.of(TransactionStatus.REJEITADA, "51"),
                Arguments.of(TransactionStatus.ERRO, "07"));
    }

    @ParameterizedTest
    @MethodSource("transactionStatusArgument")
    void transactionStatusCodeTest(final TransactionStatus status, final String expected) {
        // given

        // when
        final var actual = status.getCode();

        // then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

}
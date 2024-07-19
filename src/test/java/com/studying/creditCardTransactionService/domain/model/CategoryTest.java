package com.studying.creditCardTransactionService.domain.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class CategoryTest {

    @ParameterizedTest
    @MethodSource("mccArguments")
    void fromCodeTest(final String code, final Category expected) {
        // given

        // when
        final var actual = Category.fromCode(code);

        // then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    public static Stream<Arguments> mccArguments() {
        return Stream.of(
                Arguments.of("5411", Category.FOOD),
                Arguments.of("5412", Category.FOOD),
                Arguments.of("5811", Category.MEAL),
                Arguments.of("5812", Category.MEAL),
                Arguments.of("9999", Category.CASH),
                Arguments.of("invalid", Category.CASH)
                        );
    }
}
package com.studying.creditCardTransactionService.view.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class CreditCardTransactionRequestDtoTest {

    private static final UUID ID = UUID.randomUUID();
    private static final String ACCOUNT_ID = "some account id";
    private static final BigDecimal AMOUNT = new BigDecimal("0.01");
    private static final String MERCHANT = "some merchant";
    private static final String MCC = "9999";
    private static final String BLANK = " ".repeat(5);

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void validDtoTest() {
        // given
        var dto = new CreditCardTransactionRequestDto(ID, ACCOUNT_ID, AMOUNT, MERCHANT, MCC);

        // when
        var violations = validator.validate(dto);

        // then
        assertThat(violations).isEmpty();

        assertThat(dto.id()).isEqualTo(ID);
        assertThat(dto.accountId()).isEqualTo(ACCOUNT_ID);
        assertThat(dto.amount()).isEqualTo(AMOUNT);
        assertThat(dto.merchant()).isEqualTo(MERCHANT);
        assertThat(dto.mcc()).isEqualTo(MCC);
    }

    @ParameterizedTest
    @MethodSource("invalidDtoArgument")
    void invalidDtoTest(UUID id,  String accountId, BigDecimal amount, String merchant,
                        String mcc, int expectedViolation) {
        // given
        var dto = new CreditCardTransactionRequestDto(id, accountId, amount, merchant, mcc);

        // when
        var violations = validator.validate(dto);

        // then
        assertThat(violations).hasSize(expectedViolation);
    }

    private static Stream<Arguments> invalidDtoArgument() {
        return Stream.of(
                // null validations
                Arguments.of(null, ACCOUNT_ID, AMOUNT, MERCHANT, MCC, 1),
                Arguments.of(ID, null, AMOUNT, MERCHANT, MCC, 1),
                Arguments.of(ID, ACCOUNT_ID, null, MERCHANT, MCC, 1),
                Arguments.of(ID, ACCOUNT_ID, AMOUNT, null, MCC, 1),
                Arguments.of(ID, ACCOUNT_ID, AMOUNT, MERCHANT, null, 1),

                // blank validations
                Arguments.of(ID, BLANK, AMOUNT, MERCHANT, MCC, 1),
                Arguments.of(ID, BLANK, AMOUNT, MERCHANT, MCC, 1),
                Arguments.of(ID, ACCOUNT_ID, AMOUNT, BLANK, MCC, 1),
                Arguments.of(ID, ACCOUNT_ID, AMOUNT, MERCHANT, BLANK, 2),

                // big decimal validations
                Arguments.of(ID, ACCOUNT_ID, new BigDecimal("0.00"), MERCHANT, MCC, 1),
                Arguments.of(ID, ACCOUNT_ID, new BigDecimal("-0.01"), MERCHANT, MCC, 1),
                Arguments.of(ID, ACCOUNT_ID, new BigDecimal("0.123"), MERCHANT, MCC, 1)
                        );
    }
}
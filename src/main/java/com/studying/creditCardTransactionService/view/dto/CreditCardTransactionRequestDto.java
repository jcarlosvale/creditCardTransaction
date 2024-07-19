package com.studying.creditCardTransactionService.view.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.UUID;

public record CreditCardTransactionRequestDto(@NotNull(message = "id should not be null")
                                              UUID id,
                                              @NotBlank(message = "accountId should not be null or blank")
                                              String accountId,
                                              @DecimalMin(value = "0.00", inclusive = false,
                                                      message = "amount should be positive")
                                              @Digits(integer = 10, fraction = 2,
                                                      message = "amount should have a max of 2 decimal places")
                                              @NotNull(message = "amount should not be null")
                                              BigDecimal amount,
                                              @NotBlank(message = "merchant should not be blank")
                                              String merchant,
                                              @NotBlank(message = "mcc should not be blank")
                                              @Pattern(regexp = "\\d{4}", message = "mcc should be a 4 digits string")
                                              String mcc) {
}

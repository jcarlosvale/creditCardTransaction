package com.studying.creditCardTransactionService.domain.model;

import lombok.Getter;

public enum TransactionStatus {
    APROVADA("00"), REJEITADA("51"), ERRO("07");

    @Getter
    private final String code;

    TransactionStatus(final String code) {
        this.code = code;
    }
}

package com.studying.creditCardTransactionService.domain.service;

import com.studying.creditCardTransactionService.domain.model.CreditCardTransaction;

public interface TransactionService {
    String register(CreditCardTransaction transaction);
}

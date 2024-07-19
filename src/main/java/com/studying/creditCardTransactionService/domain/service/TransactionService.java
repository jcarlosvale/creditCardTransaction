package com.studying.creditCardTransactionService.domain.service;

import com.studying.creditCardTransactionService.domain.dto.CreditTransactionDto;
import com.studying.creditCardTransactionService.domain.model.TransactionStatus;

public interface TransactionService {
    TransactionStatus registerTransaction(CreditTransactionDto transactionDto);
}

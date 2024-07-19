package com.studying.creditCardTransactionService.view.rs;

import com.studying.creditCardTransactionService.domain.service.TransactionService;
import com.studying.creditCardTransactionService.view.dto.CreditCardTransactionRequestDto;
import com.studying.creditCardTransactionService.view.dto.CreditCardTransactionResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/transactions")
public class CreditCardTransactionController {

    private final TransactionService service;

    @PostMapping
    public ResponseEntity<CreditCardTransactionResponseDto> registerTransaction(
            @Valid @RequestBody final CreditCardTransactionRequestDto creditCardTransactionRequestDto) {

        final var creditTransactionDto = creditCardTransactionRequestDto.toCreditTransactionDto();
        final var id = creditCardTransactionRequestDto.id();
        final var transactionStatus = service.registerTransaction(creditTransactionDto);
        final var creditCardTransactionResponseDto = new CreditCardTransactionResponseDto(id, transactionStatus.getCode());

        return ResponseEntity.ok(creditCardTransactionResponseDto);
    }

}

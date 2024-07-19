package com.studying.creditCardTransactionService.domain.service.impl;

import com.studying.creditCardTransactionService.domain.model.CategoryOfMerchant;
import com.studying.creditCardTransactionService.domain.repository.CategoryOfMerchantRepository;
import com.studying.creditCardTransactionService.domain.service.CategoryOfMerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryOfMerchantServiceImpl implements CategoryOfMerchantService {

    private final CategoryOfMerchantRepository repository;

    @Override
    public Optional<CategoryOfMerchant> findByMerchant(final String merchant) {
        Objects.requireNonNull(merchant, "merchant must not be null");
        return repository.findCategoryOfMerchantByMerchant(merchant);
    }
}

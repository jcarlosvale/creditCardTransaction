package com.studying.creditCardTransactionService.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryOfMerchant {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String merchant;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

}

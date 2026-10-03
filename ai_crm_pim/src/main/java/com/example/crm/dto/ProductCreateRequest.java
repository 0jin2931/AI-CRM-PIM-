package com.example.crm.dto;

import com.example.crm.domain.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductCreateRequest(
        @NotBlank String category,
        @NotBlank String name,
        @NotNull @PositiveOrZero Integer price,
        @NotNull @PositiveOrZero Integer stockQuantity) {

    public Product toEntity() {
        return Product.builder()
                .category(category)
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
    }
}

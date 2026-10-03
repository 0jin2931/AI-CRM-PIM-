package com.example.crm.dto;

import com.example.crm.domain.Product;

public record ProductResponse(Long id, String category, String name, Integer price,
                              Integer stockQuantity, Long version) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getCategory(), product.getName(),
                product.getPrice(), product.getStockQuantity(), product.getVersion());
    }
}

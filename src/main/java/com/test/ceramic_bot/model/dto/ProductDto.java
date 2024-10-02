package com.test.ceramic_bot.model.dto;

import com.test.ceramic_bot.model.entity.ProductEntity;

public record ProductDto(
        Long id, String name, String description, Double price, Integer stockQuantity
) {
    public static ProductDto from(ProductEntity productEntity) {
        return new ProductDto(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getDescription(),
                productEntity.getPrice(),
                productEntity.getStockQuantity()
        );
    }
}

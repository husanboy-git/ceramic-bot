package com.test.ceramic_bot.service;

import com.test.ceramic_bot.model.dto.ProductDto;
import com.test.ceramic_bot.model.entity.ProductEntity;
import com.test.ceramic_bot.repository.ProductRepository;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class ProductService {

    @Autowired private ProductRepository productRepository;

    @Transactional
    public ProductDto addProduct(String name, String description, Double price, Integer stockQuantity) {
        if(productRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Product with this name already exists!");
        }
        ProductEntity savedProduct = productRepository.save(ProductEntity.of(
                name, description, price, stockQuantity));
        return ProductDto.from(savedProduct);
    }

    public ProductDto getProductByName(String name) {
        ProductEntity productEntity = productRepository.findByName(name).orElseThrow(
                () -> new IllegalArgumentException("Product not found!"));
        return ProductDto.from(productEntity);
    }

    @Transactional
    public ProductDto updateProduct(String name, String description, Double price, Integer stockQuantity) {
        ProductEntity productEntity = productRepository.findByName(name).orElseThrow(
                () -> new IllegalArgumentException("Product not found!"));
        if(ObjectUtils.isEmpty(productEntity)) {
            throw new IllegalArgumentException();
        }
        productEntity.setName(name);
        productEntity.setDescription(description);
        productEntity.setPrice(price);
        productEntity.setStockQuantity(stockQuantity);
        ProductEntity updatedEntity = productRepository.save(productEntity);
        return ProductDto.from(updatedEntity);
    }

    public List<ProductDto> getAllProduct() {
        List<ProductEntity> products = productRepository.findAll();
        return products.stream().map(ProductDto::from).toList();
    }

    public void deleteProduct(Long id) {
        ProductEntity productEntity = productRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Product Not Found!"));
        productRepository.delete(productEntity);
    }
}

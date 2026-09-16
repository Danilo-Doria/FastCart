package com.danilodoria.server.mappers;

import com.danilodoria.server.dto.request.ProductRequestDTO;
import com.danilodoria.server.dto.response.ProductResponseDTO;
import com.danilodoria.server.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductResponseDTO toResponseDTO(Product product) {
        if (product == null) return null;

        Long categoryId = product.getCategory() != null ? product.getCategory().getId() : null;
        String categoryNombre = product.getCategory() != null ? product.getCategory().getName() : null;

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                categoryId,
                categoryNombre
        );
    }

    public Product toEntity(ProductRequestDTO dto) {
        if (dto == null) return null;

        return Product.builder()
                .name(dto.name())
                .price(dto.price())
                .build();
    }
}

package com.danilodoria.server.mappers;

import com.danilodoria.server.dto.request.CategoryRequestDTO;
import com.danilodoria.server.dto.response.CategoryResponseDTO;
import com.danilodoria.server.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponseDTO toResponseDTO(Category category) {
        if (category == null) return null;
        return new CategoryResponseDTO(category.getId(), category.getName());
    }

    public Category toEntity(CategoryRequestDTO dto) {
        if (dto == null) return null;
        return Category.builder()
                .name(dto.name())
                .build();
    }
}

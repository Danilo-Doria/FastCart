package com.danilodoria.server.service;

import com.danilodoria.server.dto.request.CategoryRequestDTO;
import com.danilodoria.server.dto.response.CategoryResponseDTO;
import com.danilodoria.server.entity.Category;
import com.danilodoria.server.entity.Product;
import com.danilodoria.server.exception.ResourceNotFoundException;
import com.danilodoria.server.mappers.CategoryMapper;
import com.danilodoria.server.repository.CategoryRepository;
import com.danilodoria.server.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productoRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponseDTO> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponseDTO)
                .toList();
    }

    public CategoryResponseDTO findById(Long id) {
        return categoryMapper.toResponseDTO(findEntityById(id));
    }

    public List<CategoryResponseDTO> findByCategoryName(String categoryName) {
        return categoryRepository.findByNameContainingIgnoreCase(categoryName)
                .stream()
                .map(categoryMapper::toResponseDTO)
                .toList();
    }

    public CategoryResponseDTO save(CategoryRequestDTO dto) {
        Category category = categoryMapper.toEntity(dto);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponseDTO(savedCategory);
    }

    public CategoryResponseDTO update(Long id, CategoryRequestDTO dto) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    existingCategory.setName(dto.name());
                    Category updated = categoryRepository.save(existingCategory);
                    return categoryMapper.toResponseDTO(updated);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el ID: " + id));
    }

    @Transactional // Garantiza que si algo falla, se revierten los cambios
    public void remove(Long id) {
        // Verifica que la categoría exista (o lanza 404)
        Category category = findEntityById(id);

        // Obtiene todos los productos asociados a esta categoría
        List<Product> products = productoRepository.findByCategoryId(id);

        // Les quita la categoría (pone la relación en null)
        for (Product product : products) {
            product.setCategory(null);
            productoRepository.save(product);
        }

        // Borra la categoría de forma segura
        categoryRepository.delete(category);
    }

    // Método auxiliar privado para consumo interno de la Entidad
    private Category findEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el ID: " + id));
    }
}
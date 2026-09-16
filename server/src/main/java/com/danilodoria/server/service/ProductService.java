package com.danilodoria.server.service;

import com.danilodoria.server.dto.request.ProductRequestDTO;
import com.danilodoria.server.dto.response.ProductResponseDTO;
import com.danilodoria.server.entity.Category;
import com.danilodoria.server.entity.Product;
import com.danilodoria.server.exception.ResourceNotFoundException;
import com.danilodoria.server.mappers.ProductMapper;
import com.danilodoria.server.repository.CategoryRepository;
import com.danilodoria.server.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    // Recibe DTO de entrada, busca la categoría, mapea a Entidad y guarda
    public ProductResponseDTO save(ProductRequestDTO dto) {
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el ID: " + dto.categoryId()));

        Product product = productMapper.toEntity(dto);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }

    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el ID: " + dto.categoryId()));

        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(dto.name());
                    existingProduct.setPrice(dto.price());
                    existingProduct.setCategory(category);
                    Product updated = productRepository.save(existingProduct);
                    return productMapper.toResponseDTO(updated);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
    }

    public void remove(Long id) {
        Product producto = findEntityById(id);
        productRepository.delete(producto);
    }

    public ProductResponseDTO findById(Long id) {
        return productMapper.toResponseDTO(findEntityById(id));
    }

    private Product findEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
    }

    public List<ProductResponseDTO> findByCategoryName(String categoryName) {
        return productRepository.findByCategoryNameContainingIgnoreCase(categoryName)
                .stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

    public List<ProductResponseDTO> findAll() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

    public List<ProductResponseDTO> findByCategoryId(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada con el ID: " + id);
        }
        return productRepository.findByCategoryId(id)
                .stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }
}
package com.danilodoria.server.controller;

import com.danilodoria.server.dto.request.ProductRequestDTO;
import com.danilodoria.server.dto.response.ProductResponseDTO;
import com.danilodoria.server.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/categoria/{categoryId}")
    public ResponseEntity<List<ProductResponseDTO>> findByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.findByCategoryId(categoryId));
    }

    @GetMapping("/buscar-categoria")
    public ResponseEntity<List<ProductResponseDTO>> findByCategory(@RequestParam(name = "nombre") String categoryName) {
        return ResponseEntity.ok(productService.findByCategoryName(categoryName));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> save(@Valid @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO createdProduct = productService.save(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdProduct.id())
                .toUri();

        return ResponseEntity.created(location).body(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO dto) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        productService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
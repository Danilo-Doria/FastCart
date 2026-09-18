package com.danilodoria.server.controller;

import com.danilodoria.server.dto.request.ProductRequestDTO;
import com.danilodoria.server.dto.response.ProductResponseDTO;
import com.danilodoria.server.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Endpoints for managing products"
)
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "Get all products",
            description = "Returns a list containing all registered products."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully"
            )
    })
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @Operation(
            summary = "Get product by ID",
            description = "Returns a product identified by its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product found successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findById(
            @Parameter(
                    description = "Unique identifier of the product",
                    example = "1"
            )
            @PathVariable Long id) {

        return ResponseEntity.ok(productService.findById(id));
    }

    @Operation(
            summary = "Get products by category ID",
            description = "Returns all products that belong to the specified category."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully"
            )
    })
    @GetMapping("/categoria/{categoryId}")
    public ResponseEntity<List<ProductResponseDTO>> findByCategory(
            @Parameter(
                    description = "Unique identifier of the category",
                    example = "1"
            )
            @PathVariable Long categoryId) {

        return ResponseEntity.ok(productService.findByCategoryId(categoryId));
    }

    @Operation(
            summary = "Search products by category name",
            description = "Returns all products belonging to a category identified by its name."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully"
            )
    })
    @GetMapping("/buscar-categoria")
    public ResponseEntity<List<ProductResponseDTO>> findByCategory(
            @Parameter(
                    description = "Name of the category to search for",
                    example = "Electronics"
            )
            @RequestParam(name = "nombre") String categoryName) {

        return ResponseEntity.ok(productService.findByCategoryName(categoryName));
    }

    @Operation(
            summary = "Create a product",
            description = "Creates a new product using the information provided in the request body."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product data"
            )
    })
    @PostMapping
    public ResponseEntity<ProductResponseDTO> save(
            @Valid @RequestBody ProductRequestDTO dto) {

        ProductResponseDTO createdProduct = productService.save(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdProduct.id())
                .toUri();

        return ResponseEntity.created(location).body(createdProduct);
    }

    @Operation(
            summary = "Update a product",
            description = "Updates an existing product identified by its ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @Parameter(
                    description = "Unique identifier of the product",
                    example = "1"
            )
            @PathVariable Long id,

            @Valid @RequestBody ProductRequestDTO dto) {

        return ResponseEntity.ok(productService.update(id, dto));
    }

    @Operation(
            summary = "Delete a product",
            description = "Deletes an existing product identified by its ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Product deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(
            @Parameter(
                    description = "Unique identifier of the product",
                    example = "1"
            )
            @PathVariable Long id) {

        productService.remove(id);

        return ResponseEntity.noContent().build();
    }
}
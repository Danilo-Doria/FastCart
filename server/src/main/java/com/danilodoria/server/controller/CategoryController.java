package com.danilodoria.server.controller;

import com.danilodoria.server.dto.request.CategoryRequestDTO;
import com.danilodoria.server.dto.response.CategoryResponseDTO;
import com.danilodoria.server.service.CategoryService;
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
@RequestMapping("/categorias")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Endpoints for managing product categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Get all categories",
            description = "Returns a list containing all registered categories."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Categories retrieved successfully"
            )
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @Operation(
            summary = "Get category by ID",
            description = "Returns a category identified by its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category found successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> findById(
            @Parameter(
                    description = "Unique identifier of the category",
                    example = "1"
            )
            @PathVariable Long id) {

        // Si el servicio no la encuentra, lanza ResourceNotFoundException
        // y el GlobalExceptionHandler responde automáticamente con un 404 NOT FOUND.
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @Operation(
            summary = "Search categories by name",
            description = "Returns categories whose name matches the provided search term."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Categories retrieved successfully"
            )
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<CategoryResponseDTO>> findByName(
            @Parameter(
                    description = "Category name or search term",
                    example = "Electronics"
            )
            @RequestParam(name = "nombre") String categoryName) {

        return ResponseEntity.ok(categoryService.findByCategoryName(categoryName));
    }

    @Operation(
            summary = "Create a category",
            description = "Creates a new category using the information provided in the request body."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Category created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid category data"
            )
    })
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> save(@Valid @RequestBody CategoryRequestDTO dto) {

        CategoryResponseDTO createdCategory = categoryService.save(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCategory.id())
                .toUri();

        return ResponseEntity.created(location).body(createdCategory);
    }

    @Operation(
            summary = "Update a category",
            description = "Updates an existing category identified by its ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid category data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> update(
            @Parameter(
                    description = "Unique identifier of the category",
                    example = "1"
            )
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated category data",
                    required = true
            )
            @Valid @RequestBody CategoryRequestDTO dto) {

        // Si el ID no existe en la BD, el servicio lanza ResourceNotFoundException (404).
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    @Operation(
            summary = "Delete a category",
            description = "Deletes an existing category identified by its ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Category deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(
            @Parameter(
                    description = "Unique identifier of the category",
                    example = "1"
            )
            @PathVariable Long id) {

        // El servicio verifica la existencia (404 si no está) y la elimina.
        categoryService.remove(id);

        // .noContent().build() devuelve un estado HTTP 204 (NO CONTENT) sin cuerpo en la respuesta.
        return ResponseEntity.noContent().build();
    }
}


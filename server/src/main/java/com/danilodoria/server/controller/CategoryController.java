package com.danilodoria.server.controller;

import com.danilodoria.server.dto.request.CategoryRequestDTO;
import com.danilodoria.server.dto.response.CategoryResponseDTO;
import com.danilodoria.server.service.CategoryService;
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
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> findAll() {
        // Retorna 200 OK con el listado JSON en el cuerpo.
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> findById(@PathVariable Long id) {
        // Si el servicio no la encuentra, lanza ResourceNotFoundException
        // y el GlobalExceptionHandler responde automáticamente con un 404 NOT FOUND.
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<CategoryResponseDTO>> findByName(@RequestParam(name = "nombre") String categoryName) {
        return ResponseEntity.ok(categoryService.findByCategoryName(categoryName));
    }

    /**
     * @Valid Activa las validaciones definidas en el CategoryRequestDTO (@NotBlank, @Size, etc.).
     *
     * @RequestBody Le indica a Spring que tome el objeto JSON enviado en el cuerpo
     * de la petición HTTP y lo transforme automáticamente en una instancia de CategoryRequestDTO.
     * <p>
     * ServletUriComponentsBuilder Es una herramienta de Spring para construir URLs dinámicas
     * sin escribir direcciones manualmente.
     * <p>
     * fromCurrentRequest() Lee la URL desde la que se hizo la petición (ej. http://localhost:8080/api/categorias)
     * <p>
     * .path("/{id}") Le concatena la estructura del parámetro.
     * <p>
     * .buildAndExpand(createdCategory.id()) Sustituye {id} por el ID real recién generado.
     * <p>
     * .toUri() Convierte el texto estructurado en un objeto tipo URI.
     */
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> save(@Valid @RequestBody CategoryRequestDTO dto) {

        CategoryResponseDTO createdCategory = categoryService.save(dto);

        // ServletUriComponentsBuilder toma la URL actual de la petición (ej. "http://localhost:8080/api/categorias"),
        // le añade el path "/{id}" y reemplaza el parámetro con el ID del nuevo objeto.
        // Resultado de 'location': "http://localhost:8080/api/categorias/5"
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCategory.id())
                .toUri();

        // created(location): Establece el estado HTTP 201 Created e inserta el header "Location: http://.../5".
        return ResponseEntity.created(location).body(createdCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> update(@PathVariable Long id, @Valid @RequestBody CategoryRequestDTO dto) {
        // Si el ID no existe en la BD, el servicio lanza ResourceNotFoundException (404).
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        // El servicio verifica la existencia (404 si no está) y la elimina.
        categoryService.remove(id);

        // .noContent().build() devuelve un estado HTTP 204 (NO CONTENT) sin cuerpo en la respuesta.
        return ResponseEntity.noContent().build();
    }
}

package com.danilodoria.server.dto.request;


import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequestDTO(
        @NotBlank(message = "El nombre del producto no puede estar vacío")
        String name,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser un número positivo")
        BigDecimal price,

        @NotNull(message = "El ID de la categoría es obligatorio")
        Long categoryId
) {}

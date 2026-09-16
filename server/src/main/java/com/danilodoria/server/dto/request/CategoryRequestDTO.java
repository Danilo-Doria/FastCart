package com.danilodoria.server.dto.request;

import jakarta.validation.constraints.*;

public record CategoryRequestDTO(
        @NotBlank(message = "El nombre de la categoría no puede estar vacío")
        String name
) {}

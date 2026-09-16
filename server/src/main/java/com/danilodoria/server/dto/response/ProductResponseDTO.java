package com.danilodoria.server.dto.response;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Long id,
        String name,
        BigDecimal price,
        Long categoryId,
        String categoryNombre
) {}

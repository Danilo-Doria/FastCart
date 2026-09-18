package com.danilodoria.server.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequestDTO(
        @NotBlank(message = "El refreshToken es obligatorio")
        String refreshToken
) {}
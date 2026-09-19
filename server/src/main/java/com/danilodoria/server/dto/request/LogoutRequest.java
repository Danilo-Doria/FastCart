package com.danilodoria.server.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank(message = "El refreshToken es obligatorio")
        String refreshToken
) {}
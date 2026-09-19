package com.danilodoria.server.service;

import com.danilodoria.server.entity.RefreshToken;
import com.danilodoria.server.exception.InvalidRefreshTokenException;
import com.danilodoria.server.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // Obtiene desde la configuración el tiempo de expiración del refresh token en milisegundos
    @Value("${security.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    public RefreshToken create(String username) {
        RefreshToken refreshToken = RefreshToken.builder()
                // UUID.randomUUID() genera un identificador aleatorio
                .token(UUID.randomUUID().toString())
                .username(username)
                // Establece la fecha y hora en la que el refresh token dejará de ser válido
                .expiresAt(Instant.now().plusMillis(refreshTokenExpirationMs))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validate(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findById(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token inválido"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidRefreshTokenException("Refresh token expirado o revocado");
        }

        return refreshToken;
    }

    public void revoke(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}
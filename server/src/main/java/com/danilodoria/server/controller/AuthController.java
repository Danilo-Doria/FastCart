package com.danilodoria.server.controller;

import com.danilodoria.server.dto.request.LoginRequestDTO;
import com.danilodoria.server.dto.request.LogoutRequest;
import com.danilodoria.server.dto.request.RefreshRequestDTO;
import com.danilodoria.server.dto.response.AuthResponse;
import com.danilodoria.server.entity.RefreshToken;
import com.danilodoria.server.exception.InvalidRefreshTokenException;
import com.danilodoria.server.security.JwtService;
import com.danilodoria.server.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and JWT token management")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;

    @Value("${security.jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user using their username and password and returns an access token and refresh token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User authenticated successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid username or password"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequestDTO request) {
        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()));

        UserDetails user = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(issueTokens(user));
    }

    @Operation(
            summary = "Refresh access token",
            description = "Validates the provided refresh token, revokes it and generates a new access token and refresh token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tokens refreshed successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired refresh token"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequestDTO request) {
        RefreshToken current = refreshTokenService.validate(request.refreshToken());
        refreshTokenService.revoke(current);
        UserDetails user = userDetailsService.loadUserByUsername(current.getUsername());

        return ResponseEntity.ok(issueTokens(user));
    }

    @Operation(
            summary = "Log out",
            description = "Revokes the authenticated user's refresh token, preventing it from being used to obtain new access tokens without logging in again."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Session closed successfully, refresh token revoked"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh token is invalid, expired, already revoked, or does not belong to the authenticated user"
            )
    })
    // @AuthenticationPrincipal UserDetails authenticatedUser te da directo el usuario que ya autenticó
    // tu JwtAuthenticationFilter a través del SecurityContextHolder — no necesitas volver a leer
    // el header Authorization a mano.
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody LogoutRequest request,
            @AuthenticationPrincipal UserDetails authenticatedUser) {

        RefreshToken refreshToken = refreshTokenService.validate(request.refreshToken());

        if (!refreshToken.getUsername().equals(authenticatedUser.getUsername())) {
            throw new InvalidRefreshTokenException("Este refresh token no pertenece al usuario autenticado");
        }

        refreshTokenService.revoke(refreshToken);
        return ResponseEntity.noContent().build();
    }

    private AuthResponse issueTokens(UserDetails user) {

        // Genera un Access Token JWT para autenticar las peticiones del usuario.
        String accessToken = jwtService.generateAccessToken(user);

        // Genera y guarda un Refresh Token asociado al usuario.
        RefreshToken refreshToken = refreshTokenService.create(user.getUsername());

        // Construye la respuesta que contiene los tokens y la información de expiración.
        return new AuthResponse(
                accessToken,                    // Token de acceso para consumir los endpoints protegidos.
                refreshToken.getToken(),        // Token utilizado para solicitar un nuevo Access Token.
                "Bearer",                       // Tipo de autenticación utilizado en el encabezado Authorization.
                accessTokenExpirationMs / 1000  // Convierte la expiración de milisegundos a segundos.
        );
    }
}

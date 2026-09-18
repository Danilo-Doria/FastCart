package com.danilodoria.server.controller;

import com.danilodoria.server.dto.request.LoginRequestDTO;
import com.danilodoria.server.dto.request.RefreshRequestDTO;
import com.danilodoria.server.dto.response.AuthResponse;
import com.danilodoria.server.entity.RefreshToken;
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
        String accessToken = jwtService.generateAccessToken(user);

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

    private AuthResponse issueTokens(UserDetails user) {
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.create(user.getUsername());

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                accessTokenExpirationMs / 1000);
    }
}

package com.artaxer.tinybank.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserLoginDto(
        @NotNull(message = "Username must not be null")
        @Size(min = 4, message = "Username must be at least 4 characters long")
        @Schema(type = "string", example = "ardeshir")
        String username,

        @NotNull(message = "Password must not be null")
        @Schema(type = "string", example = "12345678")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password
) {}

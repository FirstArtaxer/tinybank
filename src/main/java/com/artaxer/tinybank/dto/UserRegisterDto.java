package com.artaxer.tinybank.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRegisterDto(
        @NotNull(message = "Username must not be null")
        @Size(min = 4, message = "Username must be at least 4 characters long")
        String username,
        @NotNull(message = "Password must not be null")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,
        String firstName,
        String lastName){}


package com.artaxer.tinybank.dto;

import com.artaxer.tinybank.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.Instant;

@Data
public class TransactionRequestDto {
        @Schema(type = "number", example = "10")
        Long amount;
        @Enumerated(EnumType.STRING)
        TransactionType transactionType;
        // Optional - only for transfer
        @Schema(type = "string",example = "only use for transfer otherwise it must be null")
        String transferAccountNumber;
        String description;
}

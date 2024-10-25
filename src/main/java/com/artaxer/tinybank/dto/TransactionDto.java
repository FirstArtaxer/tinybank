package com.artaxer.tinybank.dto;

import com.artaxer.tinybank.model.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.Instant;

@Data
public class TransactionDto{
        Long amount;
        Instant date;
        String accountNumber;
        String trackingNumber;
        @Enumerated(EnumType.STRING)
        TransactionType transactionType;
        String transferAccountNumber;
        String description;
}

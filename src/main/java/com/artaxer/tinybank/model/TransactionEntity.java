package com.artaxer.tinybank.model;


import com.artaxer.tinybank.dto.TransactionDto;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long amount;
    @Column(nullable = false)
    private Instant date;
    @Column(nullable = false)
    private String accountNumber;
    @Column(nullable = false)
    private String trackingNumber;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private String transferAccountNumber;
    private String description;


    public Long getSingedAmount() {
        if (this.getTransactionType().equals(TransactionType.WITHDRAW))
            return -this.amount;
        else
            return this.amount;
    }

    public TransactionDto toDto() {
        var transaction = new TransactionDto();
        transaction.setTrackingNumber(this.trackingNumber);
        transaction.setAccountNumber(this.accountNumber);
        transaction.setDescription(this.description);
        transaction.setDate(this.date);
        transaction.setAmount(this.amount);
        transaction.setTransactionType(this.transactionType);
        transaction.setTransferAccountNumber(this.transferAccountNumber);
        return transaction;
    }
}

package com.artaxer.tinybank.service;

import com.artaxer.tinybank.dto.TransactionDto;
import com.artaxer.tinybank.dto.TransactionRequestDto;
import com.artaxer.tinybank.exception.BadRequestException;
import com.artaxer.tinybank.model.TransactionEntity;
import com.artaxer.tinybank.model.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static com.artaxer.tinybank.model.TransactionType.DEPOSIT;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepo;
    private final Map<String, ReentrantLock> withdrawLocks = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    public TransactionService(TransactionRepository transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    @Transactional(readOnly = true)
    public Long getBalance(String accountNumber) {
        logger.info("get balance for account: {}", accountNumber);
        return transactionRepo.findByAccountNumber(accountNumber)
                .stream()
                .map(TransactionEntity::getSingedAmount)
                .reduce(0L, Long::sum);
    }

    @Transactional
    public TransactionDto doTransaction(TransactionRequestDto transactionDto, String accountNumber) {
        logger.info("do transaction: {}", transactionDto);
        var date = Instant.now();
        var trackingNumber = UUID.randomUUID().toString();

        switch (transactionDto.getTransactionType()) {
            case DEPOSIT: {
                return saveTransaction(transactionDto, date, trackingNumber, accountNumber).toDto();
            }
            case WITHDRAW: {
                var transaction = withdraw(transactionDto, date, trackingNumber, accountNumber);
                if (transactionDto.getTransferAccountNumber() != null) {
                    transactionDto.setTransactionType(DEPOSIT);
                    saveTransaction(transactionDto, date, trackingNumber, transactionDto.getTransferAccountNumber());
                }
                return transaction.toDto();
            }
            case null: return null;
        }
    }

    @Transactional
    public TransactionEntity withdraw(TransactionRequestDto transactionDto,
                                      Instant date,
                                      String trackingNumber,
                                      String accountNumber) {
        logger.info("withdraw for account: {}", accountNumber);
        ReentrantLock lock =
                withdrawLocks.computeIfAbsent(accountNumber, key -> new ReentrantLock(true));
        lock.lock();
        try {
            Long currentBalance = getBalance(accountNumber);
            if (currentBalance < transactionDto.getAmount()) {
                throw new BadRequestException("Insufficient balance for withdraw");
            }
            return saveTransaction(transactionDto, date, trackingNumber, accountNumber);
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public TransactionEntity saveTransaction(TransactionRequestDto transactionDto,
                                             Instant date,
                                             String trackingNumber,
                                             String accountNumber) {
        logger.info("save transaction: {}", transactionDto);
        TransactionEntity transaction = new TransactionEntity();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setDate(date);
        transaction.setAccountNumber(accountNumber);
        transaction.setTrackingNumber(trackingNumber);
        transaction.setTransactionType(transactionDto.getTransactionType());
        transaction.setDescription(transactionDto.getDescription());
        transaction.setTransferAccountNumber(transactionDto.getTransferAccountNumber());
        return transactionRepo.save(transaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionDto> getTransactionByAccountNumber(String accountNumber, PageRequest page) {
        logger.info("get transaction by account number: {}", accountNumber);
        return transactionRepo.findByAccountNumber(accountNumber, page)
                .map(transactionEntity -> transactionEntity.toDto());
    }

}
package com.artaxer.tinybank.service;

import com.artaxer.tinybank.dto.TransactionRequestDto;
import com.artaxer.tinybank.model.TransactionEntity;
import com.artaxer.tinybank.model.TransactionType;
import com.artaxer.tinybank.model.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepo;

    @BeforeEach
    void setUp() {
        transactionRepo.deleteAll();
    }

    private final String accountNumber1 = "ACC_111";
    private final String accountNumber2 = "ACC_222";

    @Test
    void testDeposit() {
        TransactionRequestDto depositDto = new TransactionRequestDto();
        depositDto.setAmount(100L);
        depositDto.setTransactionType(TransactionType.DEPOSIT);

        transactionService.doTransaction(depositDto,accountNumber1);

        Long balance = transactionService.getBalance(accountNumber1);
        assertEquals(100L, balance);
    }

    @Test
    void testWithdraw_success() {
        // First, deposit some money
        TransactionRequestDto depositDto = new TransactionRequestDto();
        depositDto.setAmount(200L);
        depositDto.setTransactionType(TransactionType.DEPOSIT);

        transactionService.doTransaction(depositDto,accountNumber1);

        // Now, attempt to withdraw
        TransactionRequestDto withdrawDto = new TransactionRequestDto();
        withdrawDto.setAmount(100L);
        withdrawDto.setTransactionType(TransactionType.WITHDRAW);

        transactionService.doTransaction(withdrawDto,accountNumber1);

        Long balance = transactionService.getBalance(accountNumber1);
        assertEquals(100L, balance);
    }

    @Test
    void testWithdraw_insufficientBalance() {
        TransactionRequestDto withdrawDto = new TransactionRequestDto();
        withdrawDto.setAmount(100L);
        withdrawDto.setTransactionType(TransactionType.WITHDRAW);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.doTransaction(withdrawDto,accountNumber1);
        });

        assertEquals("Insufficient balance for withdraw", exception.getMessage());
    }

    @Test
    void testTransfer_insufficientBalance() {

        // First, deposit some money
        TransactionRequestDto depositDto = new TransactionRequestDto();
        depositDto.setAmount(50L);
        depositDto.setTransactionType(TransactionType.DEPOSIT);

        transactionService.doTransaction(depositDto,accountNumber1);

        // Now, attempt to withdraw
        TransactionRequestDto withdrawDto = new TransactionRequestDto();
        withdrawDto.setTransferAccountNumber(accountNumber2);
        withdrawDto.setAmount(100L);
        withdrawDto.setTransactionType(TransactionType.WITHDRAW);
        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.doTransaction(withdrawDto,accountNumber1);
        });

        assertEquals("Insufficient balance for withdraw", exception.getMessage());

        assertEquals(50L, transactionService.getBalance(accountNumber1));
        assertEquals(0L, transactionService.getBalance(accountNumber2));

        List<TransactionEntity> transactionEntities = transactionRepo.findAll();

        Optional<TransactionEntity> depositTransaction = transactionEntities
                .stream()
                .filter(t -> t.getAccountNumber().equals(accountNumber2)).findFirst();

        assertEquals(Optional.empty(), depositTransaction);
    }

    @Test
    void testTransfer_success() {
        // First, deposit some money
        TransactionRequestDto depositDto = new TransactionRequestDto();
        depositDto.setAmount(500L);
        depositDto.setTransactionType(TransactionType.DEPOSIT);

        transactionService.doTransaction(depositDto,accountNumber1);

        // Now, attempt to withdraw
        TransactionRequestDto withdrawDto = new TransactionRequestDto();
        withdrawDto.setTransferAccountNumber(accountNumber2);
        withdrawDto.setAmount(100L);
        withdrawDto.setTransactionType(TransactionType.WITHDRAW);

        transactionService.doTransaction(withdrawDto,accountNumber1);

        assertEquals(400L, transactionService.getBalance(accountNumber1));
        assertEquals(100L, transactionService.getBalance(accountNumber2));

        List<TransactionEntity> transactionEntities = transactionRepo.findAll();

        Optional<TransactionEntity> depositTransaction = transactionEntities
                .stream()
                .filter(t -> t.getAccountNumber().equals(accountNumber2)).findFirst();
        Map<String, List<TransactionEntity>> groupedTransactionsByTrackingNumber = transactionEntities
                .stream()
                .collect(Collectors.groupingBy(transactionEntity -> transactionEntity.getTrackingNumber()));
        assertEquals(2, groupedTransactionsByTrackingNumber.get(depositTransaction.get().getTrackingNumber()).size());
    }

    @Test
    void testConcurrentWithdraw_sameAccount() throws InterruptedException {
        TransactionRequestDto depositDto = new TransactionRequestDto();
        depositDto.setAmount(200L);
        depositDto.setTransactionType(TransactionType.DEPOSIT);

        transactionService.doTransaction(depositDto,accountNumber1);

        Runnable withdrawTask = () -> {
            TransactionRequestDto withdrawDto = new TransactionRequestDto();
            withdrawDto.setAmount(150L);
            withdrawDto.setTransactionType(TransactionType.WITHDRAW);
            transactionService.doTransaction(withdrawDto,accountNumber1);
        };

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(withdrawTask);
        executor.submit(withdrawTask);
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        Long balance = transactionService.getBalance(accountNumber1);
        assertEquals(50, balance, "Balance should not exceed the initial deposit after concurrent withdrawals.");
    }

    @Test
    void testConcurrentWithdraw_differentAccounts() throws InterruptedException {
        TransactionRequestDto depositDto1 = new TransactionRequestDto();
        depositDto1.setAmount(200L);
        depositDto1.setTransactionType(TransactionType.DEPOSIT);

        TransactionRequestDto depositDto2 = new TransactionRequestDto();
        depositDto2.setAmount(200L);
        depositDto2.setTransactionType(TransactionType.DEPOSIT);

        transactionService.doTransaction(depositDto1,accountNumber1);
        transactionService.doTransaction(depositDto2,accountNumber2);

        Runnable withdrawTask1 = () -> {
            TransactionRequestDto withdrawDto = new TransactionRequestDto();
            withdrawDto.setAmount(150L);
            withdrawDto.setTransactionType(TransactionType.WITHDRAW);
            transactionService.doTransaction(withdrawDto,accountNumber1);
        };

        Runnable withdrawTask2 = () -> {
            TransactionRequestDto withdrawDto = new TransactionRequestDto();
            withdrawDto.setAmount(100L);
            withdrawDto.setTransactionType(TransactionType.WITHDRAW);
            transactionService.doTransaction(withdrawDto,accountNumber2);
        };

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(withdrawTask1);
        executor.submit(withdrawTask2);
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        Long balance1 = transactionService.getBalance(accountNumber1);
        Long balance2 = transactionService.getBalance(accountNumber2);

        assertEquals(50L, balance1, accountNumber1 + " should have a balance not exceeding 50 after withdrawal.");
        assertEquals(100L, balance2, accountNumber2 + " should have a balance not exceeding 100 after withdrawal.");
    }
}
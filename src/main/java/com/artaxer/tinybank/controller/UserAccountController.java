package com.artaxer.tinybank.controller;


import com.artaxer.tinybank.dto.*;
import com.artaxer.tinybank.service.TransactionService;
import com.artaxer.tinybank.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/accounts")
public class UserAccountController {

    private final UserService userService;
    private final TransactionService transactionService;

    public UserAccountController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public Page<UserAccountDto> getUserAccounts(@RequestParam(required = false, defaultValue = "0") Integer page,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        return userService.getUserAccounts(PageRequest.of(page, size));
    }

    @PreAuthorize("#accountNumber == authentication.principal.accountNumber")
    @GetMapping("/{accountNumber}/transactions")
    public Page<TransactionDto> getTransactions(@RequestParam(required = false, defaultValue = "0") Integer page,
                                                @RequestParam(required = false, defaultValue = "10") Integer size,
                                                @PathVariable String accountNumber) {
        return transactionService.getTransactionByAccountNumber(accountNumber, PageRequest.of(page, size));
    }

    @PreAuthorize("#accountNumber == authentication.principal.accountNumber")
    @PostMapping("/{accountNumber}/transactions")
    public TransactionDto doTransaction(@RequestBody TransactionRequestDto transactionDto,
                                        @PathVariable String accountNumber) {
        // if transferAccountNumber is not null and holds destination accountNumber
        // it means user want transfer money
        if (transactionDto.getTransferAccountNumber() != null) {
            //check only for existing destination account
            userService.loadUserByAccountNumber(transactionDto.getTransferAccountNumber());
        }
        return transactionService.doTransaction(transactionDto,accountNumber);
    }

    @PreAuthorize("#accountNumber == authentication.principal.accountNumber")
    @GetMapping("/{accountNumber}/balance")
    public BalanceDto getBalance(@PathVariable String accountNumber) {
        return new BalanceDto(transactionService.getBalance(accountNumber));
    }

    @PreAuthorize("#accountNumber == authentication.principal.accountNumber")
    @PatchMapping("/{accountNumber}/deactivate")
    public GlobalResponse deactivateUser(@PathVariable String accountNumber) {
        return userService.deactivateUserAccount(accountNumber);
    }

    @GetMapping("/{username}")
    public UserAccountDto getAccountNumber(@PathVariable String username) {
        var userAccount = userService.loadUserByUsername(username);
        return new UserAccountDto(username,userAccount.getAccountNumber());
    }
}

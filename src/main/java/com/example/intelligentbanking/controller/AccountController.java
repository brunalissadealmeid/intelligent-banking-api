package com.example.intelligentbanking.controller;

import com.example.intelligentbanking.dto.AccountDto;
import com.example.intelligentbanking.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Account Management", description = "APIs for managing bank accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @Operation(summary = "Create a new bank account",
               description = "Creates a new bank account for an existing user.",
               responses = {
                   @ApiResponse(responseCode = "201", description = "Account created successfully",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDto.class))),
                   @ApiResponse(responseCode = "400", description = "Invalid input data"),
                   @ApiResponse(responseCode = "404", description = "User not found"),
                   @ApiResponse(responseCode = "409", description = "Account number already exists")
               })
    public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody AccountDto accountDto) {
        AccountDto createdAccount = accountService.createAccount(accountDto);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    @GetMapping("/{accountNumber}/balance")
    @Operation(summary = "Get account balance",
               description = "Retrieves the current balance for a given account number.",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Balance retrieved successfully",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDto.class))),
                   @ApiResponse(responseCode = "404", description = "Account not found")
               })
    public ResponseEntity<AccountDto> getAccountBalance(
            @Parameter(description = "Account number to fetch balance for", required = true)
            @PathVariable String accountNumber) {
        AccountDto accountDto = accountService.getBalance(accountNumber);
        return ResponseEntity.ok(accountDto);
    }
}

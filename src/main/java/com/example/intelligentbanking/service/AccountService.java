package com.example.intelligentbanking.service;

import com.example.intelligentbanking.dto.AccountDto;
import com.example.intelligentbanking.account.Account;
import com.example.intelligentbanking.user.User;
import com.example.intelligentbanking.repository.AccountRepository;
import com.example.intelligentbanking.repository.UserRepository;
import com.example.intelligentbanking.exception.ResourceNotFoundException;
import com.example.intelligentbanking.exception.AccountAlreadyExistsException; // New exception
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID; // For generating account numbers if needed, or use a sequence

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AccountDto createAccount(AccountDto accountDto) {
        User user = userRepository.findById(accountDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + accountDto.getUserId()));

        // Check if account number already exists (if provided, otherwise generate)
        if (accountRepository.findByAccountNumber(accountDto.getAccountNumber()).isPresent()) {
            throw new AccountAlreadyExistsException("Account number " + accountDto.getAccountNumber() + " already exists.");
        }

        Account account = new Account();
        // A simple way to generate account number, can be more sophisticated
        // For now, we assume it's provided in DTO or set it here if not.
        if (accountDto.getAccountNumber() == null || accountDto.getAccountNumber().isBlank()) {
             // This is a placeholder. Real account number generation is complex.
            account.setAccountNumber(UUID.randomUUID().toString().replace("-", "").substring(0, 12));
        } else {
            account.setAccountNumber(accountDto.getAccountNumber());
        }

        account.setUser(user);
        account.setBalance(accountDto.getBalance() != null ? accountDto.getBalance() : BigDecimal.ZERO);
        account.setCurrency(accountDto.getCurrency().toUpperCase());
        // createdAt is set by @PrePersist

        Account savedAccount = accountRepository.save(account);
        return convertToDto(savedAccount);
    }

    @Transactional(readOnly = true)
    public AccountDto getBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + accountNumber));
        return convertToDto(account);
    }

    private AccountDto convertToDto(Account account) {
        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setBalance(account.getBalance());
        dto.setCurrency(account.getCurrency());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setUserId(account.getUser().getId());
        dto.setUsername(account.getUser().getUsername()); // Include username for context
        return dto;
    }

    // We might need a convertToEntity later if we update accounts
    // private Account convertToEntity(AccountDto accountDto, User user) {
    //     Account account = new Account();
    //     account.setAccountNumber(accountDto.getAccountNumber());
    //     account.setBalance(accountDto.getBalance());
    //     account.setCurrency(accountDto.getCurrency());
    //     account.setUser(user);
    //     // ID and CreatedAt are managed by JPA or database
    //     return account;
    // }
}

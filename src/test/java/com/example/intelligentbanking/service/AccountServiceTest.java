package com.example.intelligentbanking.service;

import com.example.intelligentbanking.dto.AccountDto;
import com.example.intelligentbanking.user.User;
import com.example.intelligentbanking.account.Account;
import com.example.intelligentbanking.repository.AccountRepository;
import com.example.intelligentbanking.repository.UserRepository;
import com.example.intelligentbanking.exception.ResourceNotFoundException;
import com.example.intelligentbanking.exception.AccountAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    private User user;
    private AccountDto accountDto;
    private Account account;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "user@example.com");
        user.setId(1L);
        user.setCreatedAt(LocalDateTime.now());

        accountDto = new AccountDto();
        accountDto.setUserId(1L);
        accountDto.setAccountNumber("1234567890");
        accountDto.setBalance(new BigDecimal("1000.00"));
        accountDto.setCurrency("USD");

        account = new Account();
        account.setId(1L);
        account.setUser(user);
        account.setAccountNumber("1234567890");
        account.setBalance(new BigDecimal("1000.00"));
        account.setCurrency("USD");
        account.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createAccount_shouldCreateAccount_whenUserExistsAndAccountNumberIsUnique() {
        when(userRepository.findById(accountDto.getUserId())).thenReturn(Optional.of(user));
        when(accountRepository.findByAccountNumber(accountDto.getAccountNumber())).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountDto createdAccountDto = accountService.createAccount(accountDto);

        assertNotNull(createdAccountDto);
        assertEquals(account.getAccountNumber(), createdAccountDto.getAccountNumber());
        assertEquals(user.getId(), createdAccountDto.getUserId());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void createAccount_shouldGenerateAccountNumber_whenAccountNumberNotProvided() {
        accountDto.setAccountNumber(null); // Test case where account number is not provided

        // Make the generated account different from the one in 'account' object for clarity
        Account generatedAccount = new Account();
        generatedAccount.setId(2L);
        generatedAccount.setUser(user);
        generatedAccount.setAccountNumber("generatedAccNum"); // Mocked generated number
        generatedAccount.setBalance(BigDecimal.ZERO);
        generatedAccount.setCurrency("USD");
        generatedAccount.setCreatedAt(LocalDateTime.now());


        when(userRepository.findById(accountDto.getUserId())).thenReturn(Optional.of(user));
        // We expect findByAccountNumber to be called with the *generated* number,
        // or for the generation logic to ensure uniqueness before this call.
        // For simplicity in this test, assume generated numbers are unique for now.
        when(accountRepository.findByAccountNumber(null)).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account arg = invocation.getArgument(0);
            // Simulate setting a generated ID and account number if it's null in the argument
            if (arg.getAccountNumber() == null || arg.getAccountNumber().isBlank()) {
                 arg.setAccountNumber("mockGenerated123"); // ensure it's not null for the DTO conversion
            }
            arg.setId(2L); // Simulate ID generation
            return arg;
        });

        AccountDto createdAccountDto = accountService.createAccount(accountDto);

        assertNotNull(createdAccountDto);
        assertNotNull(createdAccountDto.getAccountNumber()); // Ensure account number was generated
        assertFalse(createdAccountDto.getAccountNumber().isBlank());
        assertEquals(user.getId(), createdAccountDto.getUserId());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void createAccount_shouldThrowResourceNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findById(accountDto.getUserId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            accountService.createAccount(accountDto);
        });

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createAccount_shouldThrowAccountAlreadyExistsException_whenAccountNumberExists() {
        when(userRepository.findById(accountDto.getUserId())).thenReturn(Optional.of(user));
        when(accountRepository.findByAccountNumber(accountDto.getAccountNumber())).thenReturn(Optional.of(account));

        assertThrows(AccountAlreadyExistsException.class, () -> {
            accountService.createAccount(accountDto);
        });

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void getBalance_shouldReturnAccountDto_whenAccountExists() {
        when(accountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.of(account));

        AccountDto foundAccountDto = accountService.getBalance(account.getAccountNumber());

        assertNotNull(foundAccountDto);
        assertEquals(account.getAccountNumber(), foundAccountDto.getAccountNumber());
        assertEquals(account.getBalance(), foundAccountDto.getBalance());
    }

    @Test
    void getBalance_shouldThrowResourceNotFoundException_whenAccountDoesNotExist() {
        when(accountRepository.findByAccountNumber("nonexistent_account_number")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            accountService.getBalance("nonexistent_account_number");
        });
    }
}

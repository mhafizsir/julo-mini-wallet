package com.mhafizsir.miniwalletservice.usecase;

import com.mhafizsir.miniwalletservice.entity.TransactionLog;
import com.mhafizsir.miniwalletservice.entity.User;
import com.mhafizsir.miniwalletservice.payload.GeneralResponse;
import com.mhafizsir.miniwalletservice.service.TransactionLogService;
import com.mhafizsir.miniwalletservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WalletUsecase {

    private final UserService userService;
    private final TransactionLogService transactionLogService;


    public String initialize(String customerXid) {

        User user = userService.create(customerXid);
        return user.getToken();
    }

    public boolean isCustomerExists(String customerXid) {

        return userService.isCustomerExists(customerXid);
    }

    public User enableWallet(User user) {

        user.setEnabled(true);
        user.setEnabledAt(LocalDateTime.now());
        user.setDisabledAt(null);
        return userService.update(user);
    }

    public List<TransactionLog> getTransactions(User user) {

        return transactionLogService.getTransactionByUserId(user.getId());
    }

    public TransactionLog deposit(UUID userId, BigDecimal amount, String referenceId) {

        Optional<User> userOptional = userService.getByUserId(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();
        LocalDateTime now = LocalDateTime.now();
        TransactionLog transactionLog = transactionLogService.createDeposit(user, amount, referenceId, now);
        user.setBalance(user.getBalance().add(amount));
        user.setUpdatedAt(now);
        userService.update(user);
        return transactionLog;
    }

    public boolean isReferenceIdValid(String referenceId) {

        return !transactionLogService.isTransactionExistsByReferenceId(referenceId);
    }

    public TransactionLog withdraw(UUID userId, BigDecimal amount, String referenceId) {
        Optional<User> userOptional = userService.getByUserId(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();
        if (user.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        LocalDateTime now = LocalDateTime.now();
        TransactionLog transactionLog = transactionLogService.createWithdrawal(user, amount, referenceId, now);
        user.setBalance(user.getBalance().subtract(amount));
        user.setUpdatedAt(now);
        userService.update(user);
        return transactionLog;
    }

    public User disable(UUID userId, boolean isDisabled) {

            Optional<User> userOptional = userService.getByUserId(userId);
            if (userOptional.isEmpty()) {
                throw new RuntimeException("User not found");
            }
            User user = userOptional.get();
            user.setEnabled(isDisabled);
            user.setDisabledAt(LocalDateTime.now());
            user.setEnabledAt(null);
            return userService.update(user);
    }
}

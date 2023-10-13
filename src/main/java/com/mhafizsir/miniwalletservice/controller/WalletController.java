package com.mhafizsir.miniwalletservice.controller;

import com.mhafizsir.miniwalletservice.entity.TransactionLog;
import com.mhafizsir.miniwalletservice.entity.User;
import com.mhafizsir.miniwalletservice.payload.EnableWalletResponse;
import com.mhafizsir.miniwalletservice.payload.GeneralResponse;
import com.mhafizsir.miniwalletservice.payload.TransactionHistoryResponse;
import com.mhafizsir.miniwalletservice.usecase.WalletUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequiredArgsConstructor
public class WalletController {

    private final WalletUsecase walletUsecase;

    private final ConcurrentHashMap<UUID, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    @PostMapping("/v1/init")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> initializeAccount(@RequestParam("customer_xid") String customerXid) {

        boolean customerExists = walletUsecase.isCustomerExists(customerXid);
        if (customerExists) {
            return ResponseEntity.badRequest().body(new GeneralResponse<>("Wallet already exists"));
        }
        String token = walletUsecase.initialize(customerXid);
        return ResponseEntity.ok(new GeneralResponse<>(GeneralResponse.EStatus.SUCCESS, Map.of("token", token)));
    }

    @PostMapping("/v1/wallet")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> enableWallet(Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        if (user.getEnabled()) {
            return ResponseEntity.badRequest().body(new GeneralResponse<>("Wallet already enabled"));
        }
        user = walletUsecase.enableWallet(user);
        EnableWalletResponse wallet = new EnableWalletResponse(user);
        return ResponseEntity.ok(new GeneralResponse<>(GeneralResponse.EStatus.SUCCESS, Map.of("wallet", wallet)));
    }

    @GetMapping("/v1/wallet")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> viewBalance(Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        if (!user.getEnabled()) {
            return ResponseEntity.badRequest().body(new GeneralResponse<>("Wallet disabled"));
        }
        EnableWalletResponse wallet = new EnableWalletResponse(user);
        return ResponseEntity.ok(new GeneralResponse<>(GeneralResponse.EStatus.SUCCESS, Map.of("wallet", wallet)));
    }

    @GetMapping("/v1/wallet/transactions")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> viewTransactions(Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        if (!user.getEnabled()) {
            return ResponseEntity.badRequest().body(new GeneralResponse<>("Wallet disabled"));
        }

        List<TransactionLog> transactions = walletUsecase.getTransactions(user);
        List<TransactionHistoryResponse> results = transactions.stream().map(TransactionHistoryResponse::new).toList();
        return ResponseEntity.ok(new GeneralResponse<>(GeneralResponse.EStatus.SUCCESS, Map.of("transactions", results)));
    }

    @PostMapping("/v1/wallet/deposits")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> deposit(Authentication authentication, @RequestParam("amount") BigDecimal amount, @RequestParam("reference_id") String referenceId) {

        User user = (User) authentication.getPrincipal();
        if (!user.getEnabled()) {
            return ResponseEntity.badRequest().body(new GeneralResponse<>("Wallet disabled"));
        }

        ReentrantLock lock = getOrCreateLock(user.getId());
        lock.lock();
        try {
            boolean isReferenceIdValid = walletUsecase.isReferenceIdValid(referenceId);
            if (!isReferenceIdValid) {
                return ResponseEntity.badRequest().body(new GeneralResponse<>("Reference id already exists"));
            }

            TransactionLog transactionLog = walletUsecase.deposit(user.getId(), amount, referenceId);
            TransactionHistoryResponse result = new TransactionHistoryResponse(transactionLog);
            return ResponseEntity.ok(new GeneralResponse<>(GeneralResponse.EStatus.SUCCESS, Map.of("deposit", result)));
        } finally {
            lock.unlock();
        }
    }

    @PostMapping("/v1/wallet/withdrawals")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> withdraw(Authentication authentication, @RequestParam("amount") BigDecimal amount, @RequestParam("reference_id") String referenceId) {

        User user = (User) authentication.getPrincipal();
        if (!user.getEnabled()) {
            return ResponseEntity.badRequest().body(new GeneralResponse<>("Wallet disabled"));
        }

        ReentrantLock lock = getOrCreateLock(user.getId());
        lock.lock();
        try {
            boolean isReferenceIdValid = walletUsecase.isReferenceIdValid(referenceId);
            if (!isReferenceIdValid) {
                return ResponseEntity.badRequest().body(new GeneralResponse<>("Reference id already exists"));
            }

            TransactionLog transactionLog = walletUsecase.withdraw(user.getId(), amount, referenceId);
            TransactionHistoryResponse result = new TransactionHistoryResponse(transactionLog);
            return ResponseEntity.ok(new GeneralResponse<>(GeneralResponse.EStatus.SUCCESS, Map.of("deposit", result)));
        } finally {
            lock.unlock();
        }
    }

    @PatchMapping("/v1/wallet")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> disableWallet(@RequestParam("is_disabled") boolean isDisabled, Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        if (!user.getEnabled()) {
            return ResponseEntity.badRequest().body(new GeneralResponse<>("Wallet disabled"));
        }

        ReentrantLock lock = getOrCreateLock(user.getId());
        lock.lock();
        try {
            user = walletUsecase.disable(user.getId(), isDisabled);
            EnableWalletResponse wallet = new EnableWalletResponse(user);
            return ResponseEntity.ok(new GeneralResponse<>(GeneralResponse.EStatus.SUCCESS, Map.of("wallet", wallet)));
        } finally {
            lock.unlock();
        }
    }

    private ReentrantLock getOrCreateLock(UUID userId) {
        return userLocks.computeIfAbsent(userId, id -> new ReentrantLock());
    }
}

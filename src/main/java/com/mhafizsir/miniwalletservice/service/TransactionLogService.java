package com.mhafizsir.miniwalletservice.service;

import com.mhafizsir.miniwalletservice.entity.TransactionLog;
import com.mhafizsir.miniwalletservice.entity.User;
import com.mhafizsir.miniwalletservice.payload.TransactionHistoryResponse;
import com.mhafizsir.miniwalletservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionLogService {

    private final TransactionRepository transactionRepository;

    public List<TransactionLog> getTransactionByUserId(UUID id) {

        return transactionRepository.findAllByUserId(id);
    }

    public TransactionLog createDeposit(User user, BigDecimal amount, String referenceId, LocalDateTime now) {

        TransactionLog transactionLog = TransactionLog.builder()
                .user(user)
                .type(TransactionLog.ETransactionType.DEPOSIT.value)
                .description("Deposit to wallet")
                .status(TransactionLog.ETransactionStatus.SUCCESS.value)
                .transactedAt(now)
                .amount(amount)
                .referenceId(referenceId)
                .build();
        return transactionRepository.save(transactionLog);
    }

    public TransactionLog createWithdrawal(User user, BigDecimal amount, String referenceId, LocalDateTime now) {

        TransactionLog transactionLog = TransactionLog.builder()
                .user(user)
                .type(TransactionLog.ETransactionType.WITHDRAW.value)
                .description("Withdraw from wallet")
                .status(TransactionLog.ETransactionStatus.SUCCESS.value)
                .transactedAt(now)
                .amount(amount)
                .referenceId(referenceId)
                .build();
        return transactionRepository.save(transactionLog);
    }

    public boolean isTransactionExistsByReferenceId(String referenceId) {

        return transactionRepository.existsByReferenceId(referenceId);
    }
}

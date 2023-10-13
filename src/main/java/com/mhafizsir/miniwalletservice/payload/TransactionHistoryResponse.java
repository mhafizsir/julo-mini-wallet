package com.mhafizsir.miniwalletservice.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mhafizsir.miniwalletservice.entity.TransactionLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryResponse {

    private UUID id;
    private String status;
    @JsonProperty("transacted_at")
    private LocalDateTime transactedAt;
    private String type;
    private BigDecimal amount;
    @JsonProperty("reference_id")
    private String referenceId;

    public TransactionHistoryResponse(TransactionLog transactionLog) {
        this.id = transactionLog.getId();
        this.status = transactionLog.getStatus();
        this.transactedAt = transactionLog.getTransactedAt();
        this.type = transactionLog.getType();
        this.amount = transactionLog.getAmount();
        this.referenceId = transactionLog.getReferenceId();
    }
}

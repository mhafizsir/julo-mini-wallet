package com.mhafizsir.miniwalletservice.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mhafizsir.miniwalletservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnableWalletResponse {

    private UUID id;
    @JsonProperty("owned_by")
    private String ownedBy;
    private String status;
    private LocalDateTime enabledAt;
    private LocalDateTime disabledAt;
    private BigDecimal balance;

    public EnableWalletResponse(User user) {

        this.id = user.getId();
        this.ownedBy = user.getCustomerXid();
        this.status = user.getEnabled() ? "enabled" : "disabled";
        this.enabledAt = user.getEnabledAt();
        this.disabledAt = user.getDisabledAt();
        this.balance = user.getBalance();
    }
}

package com.mhafizsir.miniwalletservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_logs")
public class TransactionLog {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String type;

    private String description;

    private String status;

    @Column(name = "transacted_at")
    private LocalDateTime transactedAt;

    private BigDecimal amount;

    @Column(name = "reference_id", unique = true)
    private String referenceId;

    public enum ETransactionStatus {
        SUCCESS("success"),
        FAILED("failed");

        public final String value;

        ETransactionStatus(String value) {
            this.value = value;
        }
    }

    public enum ETransactionType {
        DEPOSIT("deposit"),
        WITHDRAW("withdraw");

        public final String value;

        ETransactionType(String value) {
            this.value = value;
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        TransactionLog that = (TransactionLog) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

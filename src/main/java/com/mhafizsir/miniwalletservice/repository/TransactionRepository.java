package com.mhafizsir.miniwalletservice.repository;

import com.mhafizsir.miniwalletservice.entity.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionLog, Long> {

    @Query("SELECT t FROM TransactionLog t WHERE t.user.id=:userId")
    List<TransactionLog> findAllByUserId(@Param("userId") UUID userId);

    boolean existsByReferenceId(String referenceId);
}

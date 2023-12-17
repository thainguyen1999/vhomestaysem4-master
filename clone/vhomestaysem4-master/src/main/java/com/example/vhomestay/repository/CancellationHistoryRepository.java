package com.example.vhomestay.repository;

import com.example.vhomestay.model.entity.CancellationHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CancellationHistoryRepository extends BaseRepository<CancellationHistory, Long>{
    @Query("SELECT ch FROM CancellationHistory ch JOIN ch.booking b JOIN b.household h " +
            "JOIN h.manager m JOIN m.account a WHERE a.email = :managerEmail AND b.bookingCode = :bookingCode AND ch.refundStatus = 'PENDING'")
    Optional<CancellationHistory> findCancellationHistoryByBookingCodeAndManagerEmail(
            @Param("bookingCode") String bookingCode,
            @Param("managerEmail") String managerEmail);
}

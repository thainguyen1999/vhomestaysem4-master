package com.example.vhomestay.repository;

import com.example.vhomestay.model.dto.response.report.admin.ReportHouseholdPayment;
import com.example.vhomestay.model.entity.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends BaseRepository<Payment, Long>{
    @Query("SELECT p FROM Payment p JOIN p.booking b WHERE b.bookingCode = :bookingCode")
    Payment findByBookingCode(String bookingCode);
    @Query("SELECT p FROM Payment p JOIN p.booking b " +
            "JOIN b.household h JOIN h.manager m " +
            "JOIN m.account a WHERE a.email = :email AND b.bookingCode = :bookingCode AND p.status = 'UNPAID'")
    Optional<Payment> findPaymentByBookingCodeAndManagerEmail(
            @Param("bookingCode") String bookingCode,
            @Param("email") String emailManager);
    @Query("SELECT new com.example.vhomestay.model.dto.response.report.admin.ReportHouseholdPayment(" +
            "h.householdName, CONCAT(m.firstName, ' ', m.lastName), a.email, m.phoneNumber, sum (p.amount)) FROM " +
            "Payment p join p.booking b join b.household h join h.manager m join m.account a " +
            "where p.status = 'PAID' and p.gateway = 'VN_PAY' and p.paymentDate >= :checkInDate " +
            "and p.paymentDate <= :checkOutDate GROUP BY h.householdName")
    List<ReportHouseholdPayment> getReportHouseholdPayment(LocalDateTime checkInDate, LocalDateTime checkOutDate);
}

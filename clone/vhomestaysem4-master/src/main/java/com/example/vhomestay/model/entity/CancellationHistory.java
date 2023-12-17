package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.example.vhomestay.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class CancellationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime cancellationDate;
    private String cancellationReason;
    private BigDecimal refundAmount;
    private LocalDateTime refundDate;
    @Enumerated(EnumType.STRING)
    private RefundStatus refundStatus;
    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @JsonBackReference
    private Customer customer;
    @OneToOne
    @JoinColumn(name = "booking_code", referencedColumnName = "bookingCode")
    @JsonBackReference
    private Booking booking;
}

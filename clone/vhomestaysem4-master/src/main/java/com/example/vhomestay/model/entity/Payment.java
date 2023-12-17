package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.example.vhomestay.enums.PaymentGateway;
import com.example.vhomestay.enums.PaymentStatus;
import com.example.vhomestay.enums.PaymentType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    @Nationalized
    private String description;
    @Enumerated(EnumType.STRING)
    private PaymentType type;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @Enumerated(EnumType.STRING)
    private PaymentGateway gateway;
    @OneToOne()
    @JoinColumn(name = "booking_code", referencedColumnName = "bookingCode")
    @JsonBackReference
    private Booking booking;
    @PrePersist
    public void prePersist() {
        this.paymentDate = LocalDateTime.now();
    }
}

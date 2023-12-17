package com.example.vhomestay.model.dto.response.report.manager;

import com.example.vhomestay.enums.PaymentGateway;
import com.example.vhomestay.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingReportDetailForManagerResponse {
    private String bookingCode;
    private LocalDateTime checkInDate;
    private String customerName;
    private Integer totalCustomer;
    private Long bookedNight;
    private BigDecimal totalRevenue;
    private LocalDateTime paymentDate;
    private PaymentType paymentMethod;
    private PaymentGateway gateway;
    private String note;
    private BigDecimal refundAmount;
    private LocalDateTime cancellationDate;
    private LocalDateTime refundDate;
    private String BookingStatus;
}

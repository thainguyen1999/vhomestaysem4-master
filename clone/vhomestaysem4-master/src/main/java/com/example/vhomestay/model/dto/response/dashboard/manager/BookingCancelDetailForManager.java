package com.example.vhomestay.model.dto.response.dashboard.manager;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BookingCancelDetailForManager {
    private String bookingCode;
    private LocalDateTime createdDate;
    private String customerFirstName;
    private String customerLastName;
    private BigDecimal refundAmount;

    public BookingCancelDetailForManager(String bookingCode, LocalDateTime createdDate, String customerFirstName, String customerLastName, BigDecimal refundAmount) {
        this.bookingCode = bookingCode;
        this.createdDate = createdDate;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.refundAmount = refundAmount;
    }
}

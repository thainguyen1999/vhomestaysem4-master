package com.example.vhomestay.model.dto.request.booking;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookingCancelCustomerRequestDto {
    private String bookingCode;
    private BigDecimal refundAmount;
    private String cancelReason;
    private String status;
    private String accountNumber;
    private String bankName;
    private String accountOwnerName;
}

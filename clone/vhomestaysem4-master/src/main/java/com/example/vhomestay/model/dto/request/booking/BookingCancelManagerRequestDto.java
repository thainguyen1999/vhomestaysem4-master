package com.example.vhomestay.model.dto.request.booking;

import com.example.vhomestay.enums.RefundStatus;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class BookingCancelManagerRequestDto {
    private String bookingCode;
    private BigDecimal refundAmount;
    private String cancelReason;
    private RefundStatus refundStatus;
}

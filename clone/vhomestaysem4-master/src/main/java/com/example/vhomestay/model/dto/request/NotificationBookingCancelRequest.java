package com.example.vhomestay.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NotificationBookingCancelRequest {
    private String title;
    private String customerName;
    private String bookingCode;
    private BigDecimal refundAmount;
    private LocalDate deadlineRefundDate;
}

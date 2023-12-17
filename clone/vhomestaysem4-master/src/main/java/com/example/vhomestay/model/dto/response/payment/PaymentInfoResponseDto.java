package com.example.vhomestay.model.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInfoResponseDto {
    private String bookingCode;
    private String houseName;
    private LocalDateTime payDate;
    private String description;
    private String amount;
    private String paymentMethod;
    private String bankCode;
    private String paymentStatus;
    private String message;
}

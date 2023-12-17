package com.example.vhomestay.model.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {
    private String bookingCode;
    private String paymentGateway;
    private String status;
    private String paymentUrl;
    private String message;

}

package com.example.vhomestay.service;

import com.example.vhomestay.model.dto.response.payment.PaymentResponseDto;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

public interface PaymentService {
    PaymentResponseDto createVNPayPayment(String bookingCode, BigDecimal totalPrice, String ip) throws UnsupportedEncodingException;

    String getPaymentStatus(String bookingCode);

    String getPaymentVNPayMessage(String responseCode);

    boolean checkTransactionResultVNPayAndUpdatePayment(String amount, String bankCode, String bankTranNo, String cardType, String orderInfo, String payDate, String responseCode, String tmnCode, String transactionNo, String transactionStatus, String bookingCode, String vnpSecureHash) throws MessagingException;

    boolean editPaymentByManager(String bookingCode);
}

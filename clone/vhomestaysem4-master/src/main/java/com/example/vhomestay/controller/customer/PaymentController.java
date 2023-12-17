package com.example.vhomestay.controller.customer;

import com.example.vhomestay.model.dto.response.payment.PaymentInfoResponseDto;
import com.example.vhomestay.service.BookingForCustomerService;
import com.example.vhomestay.service.PaymentService;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


@RestController
@RequestMapping("/api/v1/customers/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final MessageSource messageSource;
    private final PaymentService paymentService;
    private final BookingForCustomerService bookingForCustomerService;

    @GetMapping("/vnpay")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?> transactionResult(@RequestParam(value = "vnp_Amount", required = false) String amount,
                                               @RequestParam(value = "vnp_BankCode", required = false) String bankCode,
                                               @RequestParam(value = "vnp_BankTranNo", required = false) String bankTranNo,
                                               @RequestParam(value = "vnp_CardType", required = false) String cardType,
                                               @RequestParam(value = "vnp_OrderInfo", required = false) String orderInfo,
                                               @RequestParam(value = "vnp_PayDate", required = false) String payDate,
                                               @RequestParam(value = "vnp_ResponseCode", required = false) String responseCode,
                                               @RequestParam(value = "vnp_TmnCode", required = false) String tmnCode,
                                               @RequestParam(value = "vnp_TransactionNo", required = false) String transactionNo,
                                               @RequestParam(value = "vnp_TransactionStatus", required = false) String transactionStatus,
                                               @RequestParam(value = "vnp_TxnRef", required = false) String bookingCode,
                                               @RequestParam(value = "vnp_SecureHash", required = false) String vnp_SecureHash) throws MessagingException {
        if (paymentService.checkTransactionResultVNPayAndUpdatePayment(amount, bankCode, bankTranNo, cardType,
                orderInfo, payDate, responseCode, tmnCode, transactionNo, transactionStatus, bookingCode, vnp_SecureHash)) {
            PaymentInfoResponseDto paymentInfoResponseDto = new PaymentInfoResponseDto();
            paymentInfoResponseDto.setBookingCode(bookingCode);
            paymentInfoResponseDto.setHouseName(bookingForCustomerService.findHouseNameByBookingCode(bookingCode));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            LocalDateTime paymentDate = LocalDateTime.parse(payDate, formatter);
            paymentInfoResponseDto.setPayDate(paymentDate);
            paymentInfoResponseDto.setDescription(orderInfo.replaceAll("//+", " "));
            paymentInfoResponseDto.setPaymentMethod(cardType);
            paymentInfoResponseDto.setBankCode(bankCode);
            paymentInfoResponseDto.setAmount(amount);
            paymentInfoResponseDto.setPaymentStatus(paymentService.getPaymentStatus(bookingCode));
            paymentInfoResponseDto.setMessage(paymentService.getPaymentVNPayMessage(responseCode));
            return ResponseEntity.ok(paymentInfoResponseDto);
        }
        throw new ResourceNotFoundException("payment.error");
    }
}

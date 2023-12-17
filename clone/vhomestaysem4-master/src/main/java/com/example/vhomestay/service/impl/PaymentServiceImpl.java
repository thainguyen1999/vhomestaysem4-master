package com.example.vhomestay.service.impl;

import com.example.vhomestay.config.VNPayConfig;
import com.example.vhomestay.enums.BookingDetailStatus;
import com.example.vhomestay.enums.BookingStatus;
import com.example.vhomestay.enums.PaymentGateway;
import com.example.vhomestay.enums.PaymentStatus;
import com.example.vhomestay.model.dto.request.NotificationBookingSuccessRequest;
import com.example.vhomestay.model.dto.response.payment.PaymentResponseDto;
import com.example.vhomestay.model.entity.Booking;
import com.example.vhomestay.model.entity.Payment;
import com.example.vhomestay.repository.BookingRepository;
import com.example.vhomestay.repository.PaymentRepository;
import com.example.vhomestay.security.SecurityUtil;
import com.example.vhomestay.service.BookingDetailService;
import com.example.vhomestay.service.BookingForCustomerService;
import com.example.vhomestay.service.NotificationService;
import com.example.vhomestay.service.PaymentService;

import com.example.vhomestay.util.exception.ResourceInternalServerErrorException;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl extends BaseServiceImpl<Payment, Long, PaymentRepository>
        implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingForCustomerService bookingForCustomerService;
    private final BookingDetailService bookingDetailService;
    private final NotificationService notificationService;
    private final BookingRepository bookingRepository;

    @Override
    public PaymentResponseDto createVNPayPayment(String bookingCode, BigDecimal totalPrice, String ip) {
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(totalPrice.multiply(new BigDecimal(100)).intValue()));
        vnp_Params.put("vnp_CurrCode", VNPayConfig.vnp_CurrCode);
        vnp_Params.put("vnp_Locale", VNPayConfig.vnp_Locale);
        vnp_Params.put("vnp_TxnRef", bookingCode);
        vnp_Params.put("vnp_OrderInfo", VNPayConfig.vnp_OrderInfo + bookingCode);
        vnp_Params.put("vnp_OrderType", VNPayConfig.vnp_OrderType);
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_Returnurl);
        vnp_Params.put("vnp_IpAddr", ip);
//        vnp_Params.put("vnp_IpAddr", IPConfig.getIP());

        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).plusHours(7);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(Date.from(zonedDateTime.toInstant())));
        ZonedDateTime zonedDateTimeExp = zonedDateTime.plusMinutes(15);
        vnp_Params.put("vnp_ExpireDate", formatter.format(Date.from(zonedDateTimeExp.toInstant())));

        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + VNPayConfig.hashAllFields(vnp_Params);

        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
        paymentResponseDto.setBookingCode(bookingCode);
        paymentResponseDto.setPaymentGateway(String.valueOf(PaymentGateway.VN_PAY));
        paymentResponseDto.setStatus("00");
        paymentResponseDto.setPaymentUrl(paymentUrl);
        paymentResponseDto.setMessage("payment.response.success");

        return paymentResponseDto;
    }

    @Override
    public String
    getPaymentStatus(String bookingCode) {
        return paymentRepository.findByBookingCode(bookingCode).getStatus().toString();
    }

    @Override
    public String getPaymentVNPayMessage(String responseCode) {
        return switch (responseCode) {
            case "00" -> "Giao dịch thành công";
            case "07" -> "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường)";
            case "09" -> "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng";
            case "10" -> "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần";
            case "11", "15" -> "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch";
            case "12" -> "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa";
            case "13" -> "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch";
            case "24" -> "Giao dịch không thành công do: Khách hàng hủy giao dịch";
            case "51" -> "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch";
            case "65" -> "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày";
            case "75" -> "Ngân hàng thanh toán đang bảo trì";
            case "79" -> "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch";
            default -> "Giao dịch thất bại";
        };
    }

    @Override
    public boolean checkTransactionResultVNPayAndUpdatePayment(String amount, String bankCode, String bankTranNo, String cardType,
                                                          String orderInfo, String payDate, String responseCode, String tmnCode,
                                                          String transactionNo, String transactionStatus, String bookingCode, String vnpSecureHash) throws MessagingException {
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Amount", amount);
        vnp_Params.put("vnp_BankCode", bankCode);
        vnp_Params.put("vnp_BankTranNo", bankTranNo);
        vnp_Params.put("vnp_CardType", cardType);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_PayDate", payDate);
        vnp_Params.put("vnp_ResponseCode", responseCode);
        vnp_Params.put("vnp_TmnCode", tmnCode);
        vnp_Params.put("vnp_TransactionNo", transactionNo);
        vnp_Params.put("vnp_TransactionStatus", transactionStatus);
        vnp_Params.put("vnp_TxnRef", bookingCode);

        String signValue = VNPayConfig.hashAllFieldsToSecureHash(vnp_Params);
        if (signValue.equals(vnpSecureHash)) {
            Payment payment = paymentRepository.findByBookingCode(bookingCode);
            BigDecimal amountDecimal;
            try {
                amountDecimal = BigDecimal.valueOf(Double.parseDouble(amount));
            } catch (NumberFormatException e) {
                return false;
            }

            if (payment != null && payment.getAmount().multiply(BigDecimal.valueOf(100)).compareTo(amountDecimal) == 0) {
                payment.setDescription(orderInfo.replaceAll("//+", " "));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                LocalDateTime paymentDate = LocalDateTime.parse(payDate, formatter);
                payment.setPaymentDate(paymentDate);
                if (responseCode.equals("00")) {
                    payment.setStatus(PaymentStatus.PAID);
                    bookingForCustomerService.updateBookingStatusByBookingCode(bookingCode, BookingStatus.BOOKED);
                    bookingDetailService.updateBookingDetailStatusByBookingCode(bookingCode, BookingDetailStatus.BOOKED);
                    //Send email booking success
                    bookingForCustomerService.sendEmailBookingSuccess(bookingCode);

                    //Send notification booking success
                    Booking booking = bookingRepository.existsByBookingCode(bookingCode).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
                    NotificationBookingSuccessRequest notificationBookingSuccessRequest = new NotificationBookingSuccessRequest();
                    notificationBookingSuccessRequest.setTitle("Có đơn đặt phòng mới");
                    notificationBookingSuccessRequest.setBookingCode(booking.getBookingCode());
                    notificationBookingSuccessRequest.setCustomerName(booking.getCheckInName());
                    notificationBookingSuccessRequest.setCheckInDate(String.valueOf(booking.getCheckInDate()));
                    notificationBookingSuccessRequest.setCheckOutDate(String.valueOf(booking.getCheckOutDate()));
                    notificationBookingSuccessRequest.setTotalGuest(String.valueOf(booking.getTotalGuest()));
                    notificationService.pushBookingSuccessNotification(booking.getHousehold().getManager().getId(), notificationBookingSuccessRequest);
                } else {
                    payment.setStatus(PaymentStatus.ERROR);
                    bookingForCustomerService.updateBookingStatusByBookingCode(bookingCode, BookingStatus.DELETED);
                    bookingDetailService.updateBookingDetailStatusByBookingCode(bookingCode, BookingDetailStatus.DELETED);
                }
                paymentRepository.save(payment);
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean editPaymentByManager(String bookingCode) {
        String emailManager = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new ResourceNotFoundException("Current user login not found"));
        Optional<Payment> paymentOptional = paymentRepository.findPaymentByBookingCodeAndManagerEmail(bookingCode, emailManager);
        if (paymentOptional.isEmpty()){
            throw new ResourceNotFoundException("payment.not.found");
        }

        Payment payment = paymentOptional.get();
        payment.setStatus(PaymentStatus.PAID);

        try {
            paymentRepository.save(payment);
            return true;
        } catch (Exception e){
            throw new ResourceInternalServerErrorException("booking.notfound");
        }
    }
}

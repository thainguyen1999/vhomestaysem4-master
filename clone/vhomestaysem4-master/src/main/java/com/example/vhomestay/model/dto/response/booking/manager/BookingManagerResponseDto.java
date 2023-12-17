package com.example.vhomestay.model.dto.response.booking.manager;

import com.example.vhomestay.enums.BookingStatus;
import com.example.vhomestay.enums.PaymentStatus;
import com.example.vhomestay.enums.PaymentType;
import com.example.vhomestay.model.entity.CancellationHistory;
import com.example.vhomestay.model.entity.CustomerBankInformation;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ToString
public class BookingManagerResponseDto {
    private String bookingCode;
    private LocalDateTime createdDate;
    private String bookingCustomerName;
    private String bookingCustomerPhoneNumber;
    private Integer totalNight;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer totalGuest;
    private Integer totalRoom;
    private BigDecimal totalPrice;
    private BookingStatus bookingStatus;
    private PaymentStatus paymentStatus;
    private PaymentType paymentType;
    private LocalDateTime paymentDate;
    private CancellationHistory cancellationHistory;
    private CustomerBankInformation customerBankInformation;
}

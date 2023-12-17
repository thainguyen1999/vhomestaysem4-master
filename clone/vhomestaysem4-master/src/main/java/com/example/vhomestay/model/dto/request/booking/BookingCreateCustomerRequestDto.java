package com.example.vhomestay.model.dto.request.booking;

import com.example.vhomestay.enums.PaymentGateway;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BookingCreateCustomerRequestDto {
    private Long householdId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer totalNight;
    private Integer numberOfGuests;
    private String customerName;
    private String customerPhone;
    private List<BookingDetailCreateCustomerRequestDto> bookingDetailList;
    private BigDecimal totalPrice;
    private String paymentGateway;

}

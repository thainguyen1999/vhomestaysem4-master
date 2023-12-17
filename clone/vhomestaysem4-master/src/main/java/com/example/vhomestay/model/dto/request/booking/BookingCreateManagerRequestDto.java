package com.example.vhomestay.model.dto.request.booking;

import com.example.vhomestay.enums.PaymentStatus;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@ToString
public class BookingCreateManagerRequestDto {
    private Long householdId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer totalNight;
    private Integer totalOfGuest;
    private String checkInCustomerName;
    private String checkInCustomerPhone;
    private String checkInCustomerEmail;
    private Integer totalRoom;
    private List<BookingDetailCreateManagerRequestDto> bookingDetails;
    private BigDecimal totalPrice;
}

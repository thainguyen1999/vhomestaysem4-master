package com.example.vhomestay.model.dto.response;

import com.example.vhomestay.enums.BookingStatus;
import com.example.vhomestay.model.entity.CancellationHistory;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingResponseDto {
    private Long householdId;
    private String householdImage;
    private String bookingCode;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String householdName;
    private LocalTime householdCheckInTime;
    private LocalTime householdCheckOutTime;
    private String householdPhoneNumberFirst;
    private String householdPhoneNumberSecond;
    private String bookingCheckInName;
    private String bookingCheckInPhoneNumber;
    private Integer totalNight;
    private Integer cancellationPeriod;
    private Integer totalRoom;
    private Integer totalGuest;
    private BigDecimal totalPrice;
    private BookingStatus status;
    private CancellationHistory cancellationHistory;
    private Boolean isFeedbacked;
}

package com.example.vhomestay.model.dto.request.booking;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@ToString
public class BookingRandomDormSlotManagerRequestDto {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private List<BookingDetailRandomDormSlotManagerRequestDto> bookingDetails;
}

package com.example.vhomestay.model.dto.response.booking.manager;

import lombok.Data;

import java.util.List;

@Data
public class BookingManagerSummaryWithDetailsDto {
    private BookingManagerResponseDto bookingManagerResponseDto;
    private List<BookingSummaryDto> bookingSummaryDtos;
    private List<BookingDetailManagerResponseDto> bookingDetailManagerResponseDtos;
}

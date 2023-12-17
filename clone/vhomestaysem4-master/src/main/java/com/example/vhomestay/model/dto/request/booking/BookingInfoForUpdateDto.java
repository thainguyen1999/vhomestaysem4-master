package com.example.vhomestay.model.dto.request.booking;

import lombok.Data;

@Data
public class BookingInfoForUpdateDto {
    private String bookingCode;
    private String customerName;
    private String customerPhone;
}

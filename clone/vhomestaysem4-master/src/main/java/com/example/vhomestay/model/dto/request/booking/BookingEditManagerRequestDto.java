package com.example.vhomestay.model.dto.request.booking;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BookingEditManagerRequestDto {
    private String bookingCode;
    private String bookingCustomerName;
    private String bookingCustomerPhoneNumber;
}

package com.example.vhomestay.model.dto.response.booking.manager;

import com.example.vhomestay.enums.BookingStatus;
import com.example.vhomestay.model.dto.response.bookingdetail.BookingDetailCheckInOutTodayResponseDto;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class BookingCheckInOutTodayResponseDto {
    private String bookingCode;
    private String bookingCustomerName;
    private String bookingCustomerPhone;
    private String bookingCustomerEmail;
    private List<BookingDetailCheckInOutTodayResponseDto> bookingDetailCheckInOutTodayResponseDtos;
    private BookingStatus bookingStatus;
}

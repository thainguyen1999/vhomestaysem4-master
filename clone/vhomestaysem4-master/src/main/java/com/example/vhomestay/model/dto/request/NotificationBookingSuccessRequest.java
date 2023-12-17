package com.example.vhomestay.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NotificationBookingSuccessRequest {
    private String title;
    private String customerName;
    private String totalGuest;
    private String checkInDate;
    private String checkOutDate;
    private String bookingCode;
}

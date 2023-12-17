package com.example.vhomestay.model.dto.response.room;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class RoomTodayManagerResponseDto {
    private String checkInCustomerName;
    private String bookingCode;
    private String roomName;
    private String roomTypeName;
}

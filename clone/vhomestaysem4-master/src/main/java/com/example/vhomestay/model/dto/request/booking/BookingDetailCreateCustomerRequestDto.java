package com.example.vhomestay.model.dto.request.booking;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BookingDetailCreateCustomerRequestDto {
    private Long homestayId;
    private Long householdRoomTypeId;
    private String customerCheckInName;
}

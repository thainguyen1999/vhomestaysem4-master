package com.example.vhomestay.model.dto.request.booking;

import com.example.vhomestay.enums.BookingDetailStatus;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Data
public class BookingDetailCreateManagerRequestDto {
//    private Long bookingId;
    private Long homestayId;
    private String homestayName;
    private BigDecimal price;
    private BigDecimal subTotal;
    private Long dormSlotId;
    private Integer dormSlotName;
    private Long householdRoomTypeId;
    private String householdRoomTypeName;
    private Long roomId;
    private String roomName;
    private Boolean isDorm;
//    private BookingDetailStatus bookingDetailStatus;
}

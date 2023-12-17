package com.example.vhomestay.model.dto.request.booking;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Data
public class BookingDetailRandomDormSlotManagerRequestDto {
    private Long homestayId;
    private String homestayName;
    private BigDecimal price;
    private BigDecimal subTotal;
    private Integer totalSlotDefault;
    private Integer totalSlotSelected;
    private Long householdRoomTypeId;
    private String householdRoomTypeName;
    private Long roomId;
    private String roomName;
    private Boolean isDorm;
}

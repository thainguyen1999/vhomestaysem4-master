package com.example.vhomestay.model.dto.response.room;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class RoomSearchManagerResponseDto {
    private Long homestayId;
    private String homestayName;
    private Long householdRoomTypeId;
    private String householdRoomTypeName;
    private Integer totalSlotDefault;
    private Integer totalSlotSelected;
    private Long roomId;
    private String roomName;
    private Integer capacity;
    private Integer singleBed;
    private Integer doubleBed;
    private BigDecimal price;
    private Boolean isDorm;
}

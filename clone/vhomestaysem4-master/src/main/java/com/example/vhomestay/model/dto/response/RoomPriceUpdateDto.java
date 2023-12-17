package com.example.vhomestay.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@Builder
public class RoomPriceUpdateDto {
    private Long householdRoomTypeId;
    private String roomTypeName;
    private BigDecimal price;
    private BigDecimal priceUpdate;

    public RoomPriceUpdateDto(Long householdRoomTypeId, String roomTypeName, BigDecimal price, BigDecimal priceUpdate) {
        this.householdRoomTypeId = householdRoomTypeId;
        this.roomTypeName = roomTypeName;
        this.price = price;
        this.priceUpdate = priceUpdate;
    }
}

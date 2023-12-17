package com.example.vhomestay.model.dto.response.booking.manager;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class BookingSummaryDto {
    private Long roomTypeId;
    private String roomTypeName;
    private Long quantity;
    private BigDecimal price;

    public BookingSummaryDto(Long roomTypeId, String roomTypeName, Long quantity, BigDecimal price) {
        this.roomTypeId = roomTypeId;
        this.roomTypeName = roomTypeName;
        this.quantity = quantity;
        this.price = price;
    }
}

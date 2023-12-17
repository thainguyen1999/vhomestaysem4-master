package com.example.vhomestay.model.dto.response.booking.customer;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookingDetailRecommendDto {
    private Long homestayId;
    private String homestayCode;
    private Long roomTypeHouseholdId;
    private String roomTypeName;
    private Integer quantity;
    private BigDecimal price;
    private Integer capacity;
    private Integer singleBed;
    private Integer doubleBed;

}

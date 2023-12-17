package com.example.vhomestay.model.dto.response.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomTypeHouseholdUpdatePriceDto {
    private Long roomTypeHouseholdId;
    private String roomTypeName;
    private List<String> imageListUri;
    private Integer capacity;
    private Integer singleBed;
    private Integer doubleBed;
    private Boolean isChildrenBed;
    private List<String> facilities;
    private BigDecimal price;
    private BigDecimal priceUpdate;
}

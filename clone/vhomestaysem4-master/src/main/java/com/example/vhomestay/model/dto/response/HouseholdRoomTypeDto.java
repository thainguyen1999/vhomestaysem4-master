package com.example.vhomestay.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HouseholdRoomTypeDto {
    private Long id;
    private String roomTypeName;
    private List<FacilityDto> facilities;
    private Integer singleBed;
    private Integer doubleBed;
    private Boolean isChildrenAndBed;
    private BigDecimal price;
    private Integer capacity;
}

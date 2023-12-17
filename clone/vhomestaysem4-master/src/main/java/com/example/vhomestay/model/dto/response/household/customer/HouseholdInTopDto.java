package com.example.vhomestay.model.dto.response.household.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseholdInTopDto {
    private Long id;
    private String householdName;
    private String homestay;
    private String householdCoverImage;
    private Double rateAverage;
    private BigDecimal price;
    private Integer top;
}

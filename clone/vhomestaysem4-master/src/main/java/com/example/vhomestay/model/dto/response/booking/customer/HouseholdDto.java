package com.example.vhomestay.model.dto.response.booking.customer;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class HouseholdDto {
    private Long householdId;
    private String householdName;
    private List<String> address;
    private String imageUri;
    private BigDecimal rating;
    private Integer numberOfReviews;
    private List<HouseholdServiceDto> householdServiceList;
    private Boolean haveDormitory;
}

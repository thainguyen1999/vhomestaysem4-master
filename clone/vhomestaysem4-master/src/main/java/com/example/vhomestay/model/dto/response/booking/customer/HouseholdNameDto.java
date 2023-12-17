package com.example.vhomestay.model.dto.response.booking.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HouseholdNameDto {
    private Long householdId;
    private String householdName;

    public HouseholdNameDto(Long householdId, String householdName) {
        this.householdId = householdId;
        this.householdName = householdName;
    }
}

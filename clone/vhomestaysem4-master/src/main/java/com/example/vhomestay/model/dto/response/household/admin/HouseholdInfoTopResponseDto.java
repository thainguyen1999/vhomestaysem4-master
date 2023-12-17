package com.example.vhomestay.model.dto.response.household.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseholdInfoTopResponseDto {
    private Long id;
    private String householdName;
    private String avatar;
    private String homestay;
    private String managerFirstName;
    private String managerLastName;
    private String managerPhoneNumber;
    private Integer top;
}

package com.example.vhomestay.model.dto.response.household.customer;

import com.example.vhomestay.enums.HouseholdStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomestayIntroductionDto {
    private Long homeStayId;
    private Long areaId;
    private String areaName;
    private String homeStayCode;
    private Long householdId;
    private String householdName;
    private String householdCoverImage;
    private String description;
    private HouseholdStatus status;
}

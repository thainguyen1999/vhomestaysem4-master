package com.example.vhomestay.model.dto.response;

import com.example.vhomestay.model.dto.response.household.HouseholdVillageInforResponseDto;
import com.example.vhomestay.model.entity.CustomerTestimonials;
import com.example.vhomestay.model.entity.VillageInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VillageInformationResponseDto {
    private List<VillageInformation> villageInformations;
    private List<CustomerTestimonials> customerTestimonials;
    private Integer totalHousehold;
    private Integer totalHomestay;
    private List<HouseholdVillageInforResponseDto> householdResponseDtos;
}

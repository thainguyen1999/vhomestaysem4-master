package com.example.vhomestay.model.dto.response.homestay.admin;

import com.example.vhomestay.model.dto.response.area.AreaAdminResponseDto;
import com.example.vhomestay.model.dto.response.household.admin.HouseholdResponseDto;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class HomestayAdminResponseDto {
    private List<AreaAdminResponseDto> areaAdminResponseDtoList;
    private List<HouseholdResponseDto> householdResponseDtoList;
}

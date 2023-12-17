package com.example.vhomestay.model.dto.response.household.admin;

import com.example.vhomestay.model.dto.response.household.admin.HouseholdDetailForAdminResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class HouseholdDetailListForAdminResponse {
    List<HouseholdDetailForAdminResponse> householdDetailListForAdmin;
}

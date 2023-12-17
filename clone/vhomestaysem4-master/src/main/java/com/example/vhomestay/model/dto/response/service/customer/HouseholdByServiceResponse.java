package com.example.vhomestay.model.dto.response.service.customer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HouseholdByServiceResponse {
    private Long householdId;
    private String householdName;
    private String householdAvatar;
    private String phoneNumberFirst;
    private String phoneNumberSecond;

    public HouseholdByServiceResponse(Long householdId, String householdName, String householdAvatar, String phoneNumberFirst, String phoneNumberSecond) {
        this.householdId = householdId;
        this.householdName = householdName;
        this.householdAvatar = householdAvatar;
        this.phoneNumberFirst = phoneNumberFirst;
        this.phoneNumberSecond = phoneNumberSecond;
    }
}

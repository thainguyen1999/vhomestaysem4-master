package com.example.vhomestay.model.dto.response.user;

import com.example.vhomestay.enums.HouseholdStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseholdInfoResponseDto {
    private String householdName;
    private String householdAvatar;
    private String householdPhone1;
    private String householdPhone2;
    private String householdEmail;
    private HouseholdStatus householdStatus;
    private List<String> homestayCode;
}

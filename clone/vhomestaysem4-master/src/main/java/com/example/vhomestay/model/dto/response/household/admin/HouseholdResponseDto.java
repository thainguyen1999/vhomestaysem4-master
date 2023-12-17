package com.example.vhomestay.model.dto.response.household.admin;

import lombok.Data;

import java.time.LocalTime;

@Data
public class HouseholdResponseDto {
    private Long id;
    private String householdName;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Integer cancellationPeriod;
    private String phoneNumberFirst;
    private String phoneNumberSecond;
    private String email;
    private String linkFacebook;
    private String linkTiktok;
    private String description;
    private String avatar;
    private String coverImage;
    private String linkYoutube;
}

package com.example.vhomestay.model.dto.request.household;

import lombok.Data;

import java.time.LocalTime;

@Data
public class HouseholdInfoRequestDto {
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
    private String linkYoutube;
}

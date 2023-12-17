package com.example.vhomestay.model.dto.response.homestay.admin;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class HomestayAreaAdminResponseDto {
    private Long homestayId;
    private String homestayCode;
    private Long householdId;
    private String householdName;
}

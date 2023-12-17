package com.example.vhomestay.model.dto.request.homestay;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class HomestayAdminRequestDto {
    private Long homestayId;
    private Long areaId;
    private Long householdId;
    @NotBlank(message = "Homestay name is required")
    private String homestayName;
}

package com.example.vhomestay.model.dto.response.facility;

import com.example.vhomestay.enums.BaseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FacilityAdminResponseDto {
    private Long id;
    private String facilityName;
    private BaseStatus status;
}

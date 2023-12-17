package com.example.vhomestay.model.dto.response;

import com.example.vhomestay.util.annotation.UniqueName;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomTypeDto {
    private Long id;
    private String roomTypeName;
    private Boolean isDorm;
    private Integer singleBed;
    private Integer doubleBed;
}

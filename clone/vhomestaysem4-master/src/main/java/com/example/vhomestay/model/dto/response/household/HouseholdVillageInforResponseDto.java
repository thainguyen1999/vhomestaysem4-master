package com.example.vhomestay.model.dto.response.household;

import com.example.vhomestay.model.dto.response.homestay.HomestayForSearchRoomManagerResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseholdVillageInforResponseDto {
    private Long id;
    private String householdName;
    private List<HomestayForSearchRoomManagerResponseDto> homestays;
}

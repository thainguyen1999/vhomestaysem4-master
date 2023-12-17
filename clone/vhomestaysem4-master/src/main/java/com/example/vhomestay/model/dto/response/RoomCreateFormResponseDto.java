package com.example.vhomestay.model.dto.response;

import com.example.vhomestay.model.entity.Homestay;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomCreateFormResponseDto {
    private List<String> roomNameList;
    private List<HouseholdRoomTypeDto> roomType;
    private List<HomestayDto> homestay;
}

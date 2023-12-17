package com.example.vhomestay.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditRoomShowDetailResponseDto {
    private List<String> homeStayName;
    private List<HouseholdRoomTypeResponseDto> householdRoomTypeResponseDtoList;
    private String roomName;
    private String homeStayCodeOfRoom;
    private String roomType;
}

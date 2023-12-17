package com.example.vhomestay.model.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomCreateRequestDto {
    private List<String> roomNameList;
    private Long householdRoomTypeId;
    private Long homestayId;
}

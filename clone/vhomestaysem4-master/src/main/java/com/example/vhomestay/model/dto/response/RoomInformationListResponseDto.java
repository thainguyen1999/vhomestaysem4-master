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
public class RoomInformationListResponseDto {
    private List<RoomInformationResponseDto> roomListForManager;
}

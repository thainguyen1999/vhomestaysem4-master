package com.example.vhomestay.model.dto.response;

import com.example.vhomestay.enums.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomEditDto {
    private Long roomId;
    private String roomName;
    private RoomStatus roomStatus;
}

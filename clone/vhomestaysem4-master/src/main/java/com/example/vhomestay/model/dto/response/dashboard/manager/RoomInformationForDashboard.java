package com.example.vhomestay.model.dto.response.dashboard.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomInformationForDashboard {
    private Long roomId;
    private String roomName;
    private String roomTypeName;
    private boolean isAvailable;
    private String bookingCode;
    private String customerName;
}

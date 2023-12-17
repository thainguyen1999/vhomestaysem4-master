package com.example.vhomestay.model.dto.response.dashboard.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DormitoryInformationForDashboard {
    private Long roomId;
    private String roomName;
    private String roomTypeName;
    private int availableSlot;
    private Long totalSlot;

}

package com.example.vhomestay.model.dto.response.dashboard.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomestayInformationForDashboard {
    private Long homestayId;
    private String homestayCode;
    private List<RoomInformationForDashboard> roomInformationForDashboards;
    private List<DormitoryInformationForDashboard> dormitoryInformationForDashboards;
}

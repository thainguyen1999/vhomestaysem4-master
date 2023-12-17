package com.example.vhomestay.model.dto.response.report.manager;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomTypeReportListForManagerResponse {
    List<RoomTypeReportDetailForManagerResponse> roomTypeReportListForManager;
}

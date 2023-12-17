package com.example.vhomestay.model.dto.request.Report;

import com.example.vhomestay.model.dto.response.report.admin.ReportDetailForAdminResponse;
import com.example.vhomestay.model.dto.response.report.admin.ReportDetailListResponseForAdmin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportForAdminRequest {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private List<ReportDetailForAdminResponse> reportDetailListForAdmin;
}

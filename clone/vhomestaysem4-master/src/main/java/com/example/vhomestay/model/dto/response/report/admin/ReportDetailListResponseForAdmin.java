package com.example.vhomestay.model.dto.response.report.admin;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDetailListResponseForAdmin {
    private List<ReportDetailForAdminResponse> reportDetailListForAdmin;
}

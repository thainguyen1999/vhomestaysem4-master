package com.example.vhomestay.model.dto.response.report.manager;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingReportListForManagerResponse {
    List<BookingReportDetailForManagerResponse> bookingReportDetailListForManager;
}

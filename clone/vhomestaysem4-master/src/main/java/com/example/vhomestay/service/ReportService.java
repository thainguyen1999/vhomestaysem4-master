package com.example.vhomestay.service;

import com.example.vhomestay.model.dto.response.report.admin.ReportDetailForAdminResponse;
import com.example.vhomestay.model.dto.response.report.admin.ReportDetailListResponseForAdmin;
import com.example.vhomestay.model.dto.response.report.admin.ReportHouseholdPayment;
import com.example.vhomestay.model.dto.response.report.manager.BookingReportDetailForManagerResponse;
import com.example.vhomestay.model.dto.response.report.manager.RoomTypeReportDetailForManagerResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {
    ReportDetailListResponseForAdmin getReportForAdmin(LocalDate checkInDate, LocalDate checkOutDate);
    List<ReportHouseholdPayment> getReportHouseholdPayment(LocalDate checkInDate, LocalDate checkOutDate);
    List<BookingReportDetailForManagerResponse> getBookingReportListForManager (LocalDate checkInDate, LocalDate checkOutDate);
    List<RoomTypeReportDetailForManagerResponse> getRoomTypeDetailListReport(LocalDate checkInDate, LocalDate checkOutDate);
    List<BookingReportDetailForManagerResponse> getBookingHaveBeenCancelReportListForManager (LocalDate checkInDate, LocalDate checkOutDate);
}

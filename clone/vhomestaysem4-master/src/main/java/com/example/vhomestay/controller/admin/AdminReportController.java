package com.example.vhomestay.controller.admin;

import com.example.vhomestay.model.dto.request.Report.ReportForAdminRequest;
import com.example.vhomestay.model.dto.response.MessageResponseDto;
import com.example.vhomestay.model.dto.response.report.admin.ReportDetailListResponseForAdmin;
import com.example.vhomestay.service.ReportService;
import com.example.vhomestay.service.impl.ReportServiceImpl;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/report")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportService reportService;

    @GetMapping
    ReportDetailListResponseForAdmin getReportDetailListForAdmin(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate){
        return reportService.getReportForAdmin(checkInDate, checkOutDate);
    }

    @GetMapping("/household-payment")
    ResponseEntity<?> getReportHouseholdPayment(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate){
        Map<String, Object> response = Map.of("list", reportService.getReportHouseholdPayment(checkInDate, checkOutDate));
        return ResponseEntity.ok(response);
    }
}

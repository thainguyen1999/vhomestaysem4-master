package com.example.vhomestay.controller.manager;

import com.example.vhomestay.model.dto.response.MessageResponseDto;
import com.example.vhomestay.service.BookingDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/manager/booking-detail")
@RequiredArgsConstructor
public class BookingDetailController {
    private final BookingDetailService bookingDetailService;
    @PutMapping("/{bookingCode}/check-in")
    public ResponseEntity<?> checkInBookingDetailsById(@PathVariable String bookingCode,
                                                       @RequestBody List<Long> bookingDetailIds) {
        bookingDetailService.checkInBookingDetailsById(bookingCode, bookingDetailIds);

        return ResponseEntity.ok("checkin.success");
    }

    @PutMapping("/{bookingCode}/check-out")
    public ResponseEntity<?> checkOutBookingDetailsById(@PathVariable String bookingCode,
                                                        @RequestBody List<Long> bookingDetailIds) {
        bookingDetailService.checkOutBookingDetailsById(bookingCode, bookingDetailIds);

        return ResponseEntity.ok("checkout.success");
    }
}

package com.example.vhomestay.controller.manager;

import com.example.vhomestay.model.dto.request.HomestayRequestDto;
import com.example.vhomestay.model.dto.request.booking.BookingCancelManagerRequestDto;
import com.example.vhomestay.model.dto.request.booking.BookingCreateManagerRequestDto;
import com.example.vhomestay.model.dto.request.booking.BookingEditManagerRequestDto;
import com.example.vhomestay.model.dto.response.booking.manager.*;
import com.example.vhomestay.model.dto.response.homestay.HomestayForSearchRoomManagerResponseDto;
import com.example.vhomestay.service.BookingService;
import com.example.vhomestay.service.HomestayService;
import com.example.vhomestay.service.RoomService;
import com.example.vhomestay.util.exception.ResourceConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/manager/bookings")
@RequiredArgsConstructor
public class BookingManagerController {
    private final BookingService bookingService;
    private final RoomService roomService;
    private final HomestayService homestayService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> getBookingsByManagerEmail(@RequestParam(required = false) String searchValue,
                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) {
        List<BookingManagerResponseDto> bookingManagerResponseDtos = bookingService.findBookingsByManagerEmail(searchValue, checkInDate, checkOutDate);

        Map<String, Object> response = new HashMap<>();
        response.put("bookings", bookingManagerResponseDtos);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookingCode}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> getBookingByBookingCode(@PathVariable String bookingCode) {
        BookingManagerSummaryWithDetailsDto bookingManagerSummaryWithDetailsDto = bookingService.findBookingDetailsByManager(bookingCode);
        return ResponseEntity.ok(bookingManagerSummaryWithDetailsDto);
    }

    @GetMapping("/{bookingCode}/cancel-form")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> getBookingCancelForm(@PathVariable String bookingCode) {
        BookingCancelManagerResponseDto bookingCancelManagerResponseDto = bookingService.findBookingCancelFormByManager(bookingCode);

        return ResponseEntity.ok(bookingCancelManagerResponseDto);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> cancelBookingByBookingCode(@RequestBody BookingCancelManagerRequestDto bookingCancelManagerRequestDto) {
        bookingService.cancelBookingByManager(bookingCancelManagerRequestDto);

        Map<String, String> response = new HashMap<>();
        response.put("message", "manager.household.booking.cancel.success");

        return ResponseEntity.ok(response);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> updateBookingByManager(@RequestBody BookingEditManagerRequestDto bookingEditManagerRequestDto) {
        bookingService.updateBookingByManager(bookingEditManagerRequestDto);

        Map<String, String> response = new HashMap<>();
        response.put("message", "manager.household.booking.update.success");

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> bookingRoomByManager(@RequestBody BookingCreateManagerRequestDto bookingCreateManagerRequestDto) {
        boolean isAvailable = roomService.checkRoomAndDormSlotAvailability(bookingCreateManagerRequestDto);

        bookingService.bookingRoomByManager(bookingCreateManagerRequestDto);

        Map<String, String> response = new HashMap<>();
        response.put("message", "manager.household.booking.add.success");

        return ResponseEntity.ok(response);

    }

    @PutMapping("/{bookingCode}/check-in")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> checkInBookingByManager(@PathVariable String bookingCode) {
        bookingService.checkInBookingByManager(bookingCode);

        Map<String, String> response = new HashMap<>();
        response.put("message", "manager.household.booking.update.success");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{bookingCode}/check-out")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> checkOutBookingByManager(@PathVariable String bookingCode) {
        bookingService.checkOutBookingByManager(bookingCode);

        Map<String, String> response = new HashMap<>();
        response.put("message", "manager.household.booking.update.success");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-in-out-today")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> getBookingsCheckInOutTodayByManager(@RequestParam (required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                                                                 @RequestParam (required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                                                                 @RequestParam (required = false) Long homestayId) {

        LocalDateTime checkInDateTimeToday = LocalDateTime.now().plusHours(7);
        LocalDateTime checkOutDateTimeToday = LocalDateTime.now().plusHours(7);
        LocalDate checkInDateToday = checkInDateTimeToday.toLocalDate();
        LocalDate checkOutDateToday = checkOutDateTimeToday.toLocalDate();
        List<HomestayForSearchRoomManagerResponseDto> homestayForSearchRoomManagerResponseDtos = homestayService.showHomestaysForSearchRooms();

        // Nếu người dùng nhập checkInDate và checkOutDate thì lấy ngày đó
        if(checkInDate != null){
            checkInDateToday = checkInDate;
        }
        if(checkOutDate != null){
            checkOutDateToday = checkOutDate;
        }

        List<BookingCheckInOutTodayResponseDto> bookingsCheckIn = bookingService.findBookingsCheckInTodayByManager(checkInDateToday, homestayId);
        List<BookingCheckInOutTodayResponseDto> bookingsCheckOut = bookingService.findBookingsCheckOutTodayByManager(checkOutDateToday, homestayId);

        Map<String, Object> response = Map.of(
                "bookingsCheckIn", bookingsCheckIn,
                "bookingsCheckOut", bookingsCheckOut,
                "checkInDate", checkInDateToday,
                "checkOutDate", checkOutDateToday,
                "homestays", homestayForSearchRoomManagerResponseDtos
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-in-out-today/{bookingCode}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> getBookingCheckInOutTodayByManager(@PathVariable String bookingCode) {
        List<BookingDetailManagerResponseDto> bookingCheckInOutTodayResponseDto = bookingService.getBookingDetailCheckInOutByManager(bookingCode);

        Map<String, Object> response = new HashMap<>();
        response.put("bookingDetails", bookingCheckInOutTodayResponseDto);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{bookingCode}/refund")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> refundBookingByManager(@PathVariable String bookingCode) {
        bookingService.refundBookingByManager(bookingCode);

        return ResponseEntity.ok("payment.success: " + bookingCode);
    }
}

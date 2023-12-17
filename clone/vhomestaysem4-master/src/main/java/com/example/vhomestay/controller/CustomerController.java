package com.example.vhomestay.controller;

import com.example.vhomestay.enums.BookingStatus;
import com.example.vhomestay.model.dto.request.FeedbackRequestDto;
import com.example.vhomestay.model.dto.response.*;
import com.example.vhomestay.model.dto.response.feedback.AddFeedbackForm;
import com.example.vhomestay.model.dto.response.feedback.EditFeedbackForm;
import com.example.vhomestay.service.BookingService;
import com.example.vhomestay.service.CustomerService;
import com.example.vhomestay.service.FeedbackService;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final BookingService bookingService;
    private final FeedbackService feedbackService;
    private final MessageSource messageSource;

    @GetMapping("/my-profile")
    public ResponseEntity<?> getCustomerProfile() {
        Optional<UserResponseDto> customerFormDto = customerService.getCustomerProfile();

        return ResponseEntity.ok(customerFormDto);
    }

    @PutMapping(value = "/avatar")
    public ResponseEntity<?> updateAvatar(@RequestParam("image") MultipartFile image) throws IOException {
        String newImage = customerService.updateAvatar(image);

        Map<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("imageUpdate", newImage);

        return ResponseEntity.ok()
                .body(jsonResponse);
    }

    @DeleteMapping("/avatar")
    public ResponseEntity<?> deleteAvatar() throws IOException {
        customerService.deleteAvatar();

        Map<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("httpStatus", HttpStatus.OK.toString());
        jsonResponse.put("message", messageSource.getMessage("customer.avatar.delete.success", null, Locale.getDefault()));
        jsonResponse.put("timestamp", String.valueOf(LocalDateTime.now()));

        return ResponseEntity.ok(jsonResponse);
    }


    @PatchMapping("/my-profile")
    public ResponseEntity<?> updateCustomerProfile(@Valid @RequestBody Map<String, Object> updateFields) {
        customerService.updateCustomerProfile(updateFields);

        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("httpStatus", HttpStatus.OK);
        jsonResponse.put("message", "customer.profile.update.success");
        jsonResponse.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(jsonResponse);
    }

    @GetMapping("/my-booking")
    public BookingListResponseDto getBookingsBookedByCustomer() {
        List<BookingResponseDto> bookingFormDtos = bookingService.findBookingsByCustomerEmail();

        List<BookingResponseDto> bookings = new ArrayList<>();
        for (BookingResponseDto b : bookingFormDtos) {
            if (b.getStatus() == BookingStatus.BOOKED) {
                bookings.add(b);
            }
        }
        return BookingListResponseDto.builder()
                .bookingResponseDtos(bookings)
                .build();
    }

    @GetMapping("/my-booking-history")
    public BookingListResponseDto getBookingHistoryByCustomer() {
        List<BookingResponseDto> bookingFormDtos = bookingService.findBookingsByCustomerEmail();

        List<BookingResponseDto> bookings = new ArrayList<>();
        for (BookingResponseDto b : bookingFormDtos) {
            if (b.getStatus() == BookingStatus.CHECKED_IN || b.getStatus() == BookingStatus.CHECKED_OUT) {
                bookings.add(b);
            }
        }
        return BookingListResponseDto.builder()
                .bookingResponseDtos(bookings)
                .build();
    }

    @GetMapping("/my-booking-cancel")
    public BookingListResponseDto getBookingCancelByCustomer() {
        List<BookingResponseDto> bookingFormDtos = bookingService.findBookingsByCustomerEmail();

        List<BookingResponseDto> bookings = new ArrayList<>();
        for (BookingResponseDto b : bookingFormDtos) {
            if (b.getStatus() == BookingStatus.CANCELLED) {
                bookings.add(b);
            }
        }
        return BookingListResponseDto.builder()
                .bookingResponseDtos(bookings)
                .build();
    }

    @GetMapping("/my-feedback")
    public FeedbackListResponseDto getFeedbacksByCustomer() {
        List<FeedbackResponseDto> feedbackResponseDtos = feedbackService.getFeedbacksByEmail();
        if (feedbackResponseDtos.isEmpty()) {
            throw new ResourceNotFoundException("customer.feedback.empty");
        }

        return FeedbackListResponseDto.builder()
                .feedbackResponseDtos(feedbackResponseDtos)
                .build();
    }

    @GetMapping("/my-feedback-detail")
    public ResponseEntity<?> getFeedbackById(@RequestParam("feedbackId") Long feedbackId) {
        Optional<FeedbackResponseDto> feedbackResponseDto = feedbackService.getFeedbackById(feedbackId);

        return ResponseEntity.ok(feedbackResponseDto);
    }

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("image") MultipartFile image) throws IOException {
        customerService.uploadAvatar(image);

        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("httpStatus", HttpStatus.OK);
        jsonResponse.put("message", "customer.profile.update.success");
        jsonResponse.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(jsonResponse);
    }

    @GetMapping("/my-booking/{bookingCode}")
    public ResponseEntity<?> getBookingDetail(@PathVariable String bookingCode) {
        BookingSummaryWithDetailsDTO bookingDetailResponseDto = bookingService.findBookingDetails(bookingCode);

        return ResponseEntity.ok(bookingDetailResponseDto);
    }

    @GetMapping("/my-booking/{bookingCode}/add-feedback")
    public AddFeedbackForm addFeedback (@PathVariable String bookingCode){
        return feedbackService.showAddFeedbackForm(bookingCode);
    }

    @PostMapping("/my-booking/add-feedback")
    public ResponseEntity<?> addFeedback(@Valid @RequestBody FeedbackRequestDto feedbackRequestDto) {
        if (feedbackService.addFeedback(feedbackRequestDto.getBookingCode(), feedbackRequestDto.getContent(), feedbackRequestDto.getRating())){
            MessageResponseDto messageResponseDto = new MessageResponseDto("Add Feedback successfully", HttpStatus.OK);
            return ResponseEntity.ok(messageResponseDto);
        } else {
            throw new ResourceBadRequestException(messageSource.getMessage("customer.feedback.add.error", null, Locale.getDefault()));
        }
    }

    @GetMapping("/my-booking/{feedbackId}/edit-feedback")
    public EditFeedbackForm editFeedback(@PathVariable Long feedbackId) {
        EditFeedbackForm editFeedbackForm = feedbackService.showEditFeedbackForm(feedbackId);
        return editFeedbackForm;
    }

    @PutMapping("/my-booking/edit-feedback")
    public ResponseEntity<?> editFeedback(@Valid @RequestBody FeedbackRequestDto feedbackRequestDto) {
        if (feedbackService.editFeedback(feedbackRequestDto.getFeedbackId(), feedbackRequestDto.getContent(), feedbackRequestDto.getRating())){
            return ResponseEntity.ok("customer.avatar.delete.success");
        } else {
            throw new ResourceBadRequestException("customer.booking.edit.error");
        }
    }
}


package com.example.vhomestay.controller.guest;

import com.example.vhomestay.model.dto.response.booking.customer.HouseholdDto;
import com.example.vhomestay.model.dto.response.booking.customer.HouseholdForBookingResponseDto;
import com.example.vhomestay.model.dto.response.service.customer.ServiceResponse;
import com.example.vhomestay.service.BookingForCustomerService;
import com.example.vhomestay.service.HouseholdService;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import com.example.vhomestay.util.validation.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
public class SearchBookingController {

    private final BookingForCustomerService bookingForCustomerService;
    private final HouseholdService householdService;

    @GetMapping()
    public ResponseEntity<?> getAllHousehold() {
        List<HouseholdDto> householdListResponseDto = bookingForCustomerService.findAllHousehold();
        List<ServiceResponse> serviceList = bookingForCustomerService.findAllService();
        Map<String, Object> response = new HashMap<>();
        response.put("householdListResponseDto", householdListResponseDto);
        response.put("listService", serviceList);
        response.put("householdNameList", householdService.findAllHouseholdName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchHousehold(@RequestParam(required = false) Long householdId,
                                             @RequestParam String checkInDate,
                                             @RequestParam String checkOutDate,
                                             @RequestParam Integer numberOfGuests) {
        LocalDate checkIn = Validation.parseDate(checkInDate);
        LocalDate checkOut = Validation.parseDate(checkOutDate);
        if (checkIn == null || checkOut == null) {
            throw new ResourceNotFoundException("error.format.date");
        }
        if(numberOfGuests > 35 || checkIn.isBefore(LocalDate.now()) || checkOut.isBefore(LocalDate.now()) || !checkIn.isBefore(checkOut) || numberOfGuests <= 0) {
            throw new ResourceNotFoundException("error.format.search");
        }
        Map<String, Object> response = new HashMap<>();
        if (householdId == null || householdId == 0) {
            List<HouseholdForBookingResponseDto> householdListResponseDto = bookingForCustomerService.searchHouseholdList(checkIn, checkOut, numberOfGuests);
            response.put("householdListResponseDto", householdListResponseDto);
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            response.put("maxPrice", bookingForCustomerService.findMaxPrice(householdListResponseDto, nights));
        } else {
            HouseholdForBookingResponseDto householdResponseDto = bookingForCustomerService.searchHousehold(householdId, checkIn, checkOut, numberOfGuests);
            response.put("householdResponseDto", householdResponseDto);
        }
        List<ServiceResponse> serviceList = bookingForCustomerService.findAllService();
        response.put("listService", serviceList);
        response.put("householdNameList", householdService.findAllHouseholdName());
        return ResponseEntity.ok(response);
    }
}
package com.example.vhomestay.model.dto.response.booking.customer;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class HouseholdForBookingResponseDto {
    private Long householdId;
    private String householdName;
    private String householdDescription;
    private String phoneNumber1;
    private String phoneNumber2;
    private String checkInTime;
    private String checkOutTime;
    private List<String> address;
    private String imageUri;
    private BigDecimal rating;
    private Integer numberOfReviews;
    private List<FeedbackHouseholdDto> reviewHouseholdList;
    private List<BookingDetailRecommendDto> bookingDetailRecommendList;
    private List<HouseholdServiceDto> householdServiceList;
    private List<HomestayAndTypeRoomAvailableDto> homestayAndTypeRoomAvailableList;
    private Integer numberOfGuests;
    private Integer numberOfNight;
    private Boolean haveDormitory;
    private Boolean isSuitable;

}

package com.example.vhomestay.model.dto.response.dashboard.manager;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardForManager {
    private Integer totalHomestay;
    private Integer totalRoom;
    private Integer totalDorm;
    private Integer totalCapacity;
    private Integer totalCheckInToday;
    private Integer totalCheckOutToday;
    private Integer totalBookingToday;
    private Integer totalFeedback;
    private Double totalFeedbackScore;
    List<BookingCancelDetailForManager> bookingCancelListForManager;
    List<LowFeedbackDetailForManager> lowFeedbackListForManager;
    private String session;
    private String temperature;
    private String weather;
}

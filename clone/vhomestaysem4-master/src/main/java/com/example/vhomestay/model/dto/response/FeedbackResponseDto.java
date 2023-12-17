package com.example.vhomestay.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FeedbackResponseDto {
    private Long id;
    private String householdName;
    private Integer totalRoom;
    private Integer totalGuest;
    private BigDecimal totalPrice;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer rating;
    private String content;
}

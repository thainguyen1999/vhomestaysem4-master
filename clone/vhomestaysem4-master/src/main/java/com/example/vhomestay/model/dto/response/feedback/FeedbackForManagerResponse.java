package com.example.vhomestay.model.dto.response.feedback;

import com.example.vhomestay.enums.FeedbackStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class FeedbackForManagerResponse {
    private Long feedbackId;
    private String bookingCode;
    private Integer totalRoom;
    private Integer totalGuest;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime createdDate;
    private BigDecimal totalPrice;
    @Nationalized
    private String content;
    private int rating;
    private FeedbackStatus status;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;

    public FeedbackForManagerResponse(Long feedbackId, String bookingCode, Integer totalRoom, Integer totalGuest, LocalDate checkInDate, LocalDate checkOutDate, LocalDateTime createdDate, BigDecimal totalPrice, String content, int rating, FeedbackStatus status, String customerFirstName, String customerLastName, String customerEmail) {
        this.feedbackId = feedbackId;
        this.bookingCode = bookingCode;
        this.totalRoom = totalRoom;
        this.totalGuest = totalGuest;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.createdDate = createdDate;
        this.totalPrice = totalPrice;
        this.content = content;
        this.rating = rating;
        this.status = status;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.customerEmail = customerEmail;
    }
}

package com.example.vhomestay.model.dto.response.dashboard.manager;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class LowFeedbackDetailForManager {
    private Long feedbackId;
    private String bookingCode;
    private LocalDateTime creationDate;
    private String customerFirstName;
    private String customerLastName;
    private Integer feedbackScore;

    public LowFeedbackDetailForManager(Long feedbackId, String bookingCode, LocalDateTime creationDate, String customerFirstName, String customerLastName, Integer feedbackScore) {
        this.feedbackId = feedbackId;
        this.bookingCode = bookingCode;
        this.creationDate = creationDate;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.feedbackScore = feedbackScore;
    }
}

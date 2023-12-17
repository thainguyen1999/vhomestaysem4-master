package com.example.vhomestay.model.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Setter
@Getter
public class FeedbackRequestDto {
    private String bookingCode;
    private Long feedbackId;
    @Nationalized
    private String content;
    private int rating;
}

package com.example.vhomestay.model.dto.response.feedback;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackListForManager {
    List<FeedbackForManagerResponse> feedbackListForManager;
    Double averageRating;
}

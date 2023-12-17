package com.example.vhomestay.service;

import com.example.vhomestay.model.dto.response.feedback.FeedbackListForManager;
import org.springframework.data.domain.Pageable;

public interface ManageFeedbackService {
    FeedbackListForManager getAllFeedbackForManager(String managerEmail);
    boolean hideFeedback(Long feedbackId);
    boolean showFeedback(Long feedbackId);
}

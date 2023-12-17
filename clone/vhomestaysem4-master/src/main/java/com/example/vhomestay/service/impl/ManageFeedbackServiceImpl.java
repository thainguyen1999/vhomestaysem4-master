package com.example.vhomestay.service.impl;

import com.example.vhomestay.constant.AppConstant;
import com.example.vhomestay.enums.FeedbackStatus;
import com.example.vhomestay.model.dto.response.feedback.FeedbackForManagerResponse;
import com.example.vhomestay.model.dto.response.feedback.FeedbackListForManager;
import com.example.vhomestay.model.entity.Feedback;
import com.example.vhomestay.repository.FeedbackRepository;
import com.example.vhomestay.service.ManageFeedbackService;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ManageFeedbackServiceImpl implements ManageFeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Override
    public FeedbackListForManager getAllFeedbackForManager(String managerEmail) {
        FeedbackListForManager feedbackListForManager = new FeedbackListForManager();
        List<FeedbackForManagerResponse> feedbackForManagerResponses = feedbackRepository.getAllFeedbackByManagerEmail(managerEmail);
        feedbackListForManager.setFeedbackListForManager(feedbackForManagerResponses);
        int size = feedbackForManagerResponses.size();
        if (size != 0 ){
            int i;
            double avgRating, sum = 0;
            for (i = 0; i < size; i++) {
                sum += feedbackForManagerResponses.get(i).getRating();
            }
            avgRating = sum / size;
            DecimalFormat formatter = new DecimalFormat("#.0");
            avgRating = Double.parseDouble(formatter.format(avgRating));
            feedbackListForManager.setAverageRating(avgRating);
            return feedbackListForManager;
        }
        return null;
    }

    @Override
    public boolean hideFeedback(Long feedbackId) {
        Optional<Feedback> feedbackOptional = feedbackRepository.findById(feedbackId);
        if (feedbackOptional.isPresent()) {
            Feedback feedback = feedbackOptional.get();
            feedback.setStatus(FeedbackStatus.HIDED);
            feedbackRepository.save(feedback);
            return true;
        }
        return false;
    }

    @Override
    public boolean showFeedback(Long feedbackId) {
        Optional<Feedback> feedbackOptional = feedbackRepository.findById(feedbackId);
        if (feedbackOptional.isPresent()) {
            Feedback feedback = feedbackOptional.get();
            feedback.setStatus(FeedbackStatus.SHOWED);
            feedbackRepository.save(feedback);
            return true;
        }
        return false;
    }


}

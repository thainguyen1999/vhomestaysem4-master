package com.example.vhomestay.controller.manager;

import com.example.vhomestay.model.dto.response.MessageResponseDto;
import com.example.vhomestay.model.dto.response.feedback.FeedbackListForManager;
import com.example.vhomestay.security.SecurityUtil;
import com.example.vhomestay.service.ManageFeedbackService;
import com.example.vhomestay.service.impl.ManageFeedbackServiceImpl;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/manager/feedback")
@RequiredArgsConstructor
public class ManageFeedbackController {
    private final ManageFeedbackService manageFeedbackService;

    @GetMapping
        public FeedbackListForManager getAllFeedbackForManager() {
        Optional<String> managerEmailOptional = SecurityUtil.getCurrentUserLogin();
        String managerEmail = managerEmailOptional.get();
        FeedbackListForManager feedbackListForManager = manageFeedbackService.getAllFeedbackForManager(managerEmail);
        return feedbackListForManager;
    }

    @PutMapping("{feedbackId}/hide")
    public ResponseEntity<?> hideFeedback(@PathVariable Long feedbackId) {
        boolean result = manageFeedbackService.hideFeedback(feedbackId);
        if (result) {
            MessageResponseDto messageResponseDto = new MessageResponseDto("feedback.hide.success", HttpStatus.OK);
            return ResponseEntity.ok(messageResponseDto);
        } else {
            throw new ResourceBadRequestException("feedback.not.found");
        }
    }

    @PutMapping("{feedbackId}/show")
    public ResponseEntity<?> showFeedback(@PathVariable Long feedbackId) {
        boolean result = manageFeedbackService.showFeedback(feedbackId);
        if (result) {
            MessageResponseDto messageResponseDto = new MessageResponseDto("feedback.show.success", HttpStatus.OK);
            return ResponseEntity.ok(messageResponseDto);
        } else {
            throw new ResourceBadRequestException("feedback.not.found");
        }
    }
}

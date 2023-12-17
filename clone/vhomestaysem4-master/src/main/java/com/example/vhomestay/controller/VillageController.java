package com.example.vhomestay.controller;

import com.example.vhomestay.model.dto.request.question.FrequentlyQuestionInHomeResponseDto;
import com.example.vhomestay.model.dto.response.ContactUsResponseDto;
import com.example.vhomestay.model.dto.response.VillageInformationResponseDto;
import com.example.vhomestay.model.entity.ContactUs;
import com.example.vhomestay.model.entity.VillageInformation;
import com.example.vhomestay.service.VillageInformationService;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/village")
@RequiredArgsConstructor
public class VillageController {
    private final VillageInformationService villageInformationService;
    @GetMapping("/information")
    public ResponseEntity<?> getVillageInformation(){
        VillageInformationResponseDto villageInformations = villageInformationService.getVillageInformation().orElseThrow(()-> new ResourceNotFoundException("Village information not found"));

        return ResponseEntity.ok(villageInformations);
    }

    @GetMapping("/contact-us")
    public ResponseEntity<?> getContactUs(){
        List<ContactUs> contactUs = villageInformationService.getContactUs();

        Map<String, Object> response = Map.of(
                "contactUs", contactUs
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/frequently-questions")
    public ResponseEntity<?> getFrequentlyQuestions(){
        List<FrequentlyQuestionInHomeResponseDto> frequentlyQuestions = villageInformationService.getFrequentlyQuestions();

        Map<String, Object> response = Map.of(
                "frequentlyQuestions", frequentlyQuestions
        );
        return ResponseEntity.ok(response);
    }
}

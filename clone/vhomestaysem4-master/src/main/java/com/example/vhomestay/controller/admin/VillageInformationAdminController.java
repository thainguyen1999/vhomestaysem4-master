package com.example.vhomestay.controller.admin;

import com.example.vhomestay.enums.FrequentlyQuestionType;
import com.example.vhomestay.model.dto.request.villageInformation.InformationAdminRequestDto;
import com.example.vhomestay.model.dto.response.MessageResponseDto;
import com.example.vhomestay.model.dto.response.VillageInformationResponseDto;
import com.example.vhomestay.model.entity.ContactUs;
import com.example.vhomestay.model.entity.FrequentlyQuestion;
import com.example.vhomestay.model.entity.VillageInformation;
import com.example.vhomestay.service.VillageInformationService;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/village")
@RequiredArgsConstructor
public class VillageInformationAdminController {
    private final VillageInformationService villageInformationService;

    @GetMapping("/contact-us")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getContactUsByAdmin() {
        List<ContactUs> contactUs = villageInformationService.getContactUs();

        Map<String, Object> response = Map.of(
                "contactUs", contactUs
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/contact-us")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateContactUsByAdmin(@RequestBody ContactUs contactUs) {
        villageInformationService.updateContactUsByAdmin(contactUs);

        return ResponseEntity.ok("contact.update.success");
    }

    @GetMapping("/information")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getVillageInformationByAdmin() {
        List<InformationAdminRequestDto> villageInformation = villageInformationService.getVillageInformationByAdmin();

        Map<String, Object> response = Map.of(
                "villageInformation", villageInformation
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/information")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateVillageInformationByAdmin(@ModelAttribute InformationAdminRequestDto informationAdminRequestDto) throws IOException {
        villageInformationService.updateVillageInformationByAdmin(informationAdminRequestDto);

        return ResponseEntity.ok("contact.update.success");
    }

    @GetMapping("/frequently-questions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getFrequentlyQuestionsByAdmin() {
        List<FrequentlyQuestion> frequentlyQuestions = villageInformationService.getFrequentlyQuestionsByAdmin();

        Map<String, Object> response = Map.of(
                "frequentlyQuestions", frequentlyQuestions
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/frequently-questions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createFrequentlyQuestionsByAdmin(@RequestBody FrequentlyQuestion frequentlyQuestion) {
        villageInformationService.createFrequentlyQuestionsByAdmin(frequentlyQuestion);

        return ResponseEntity.ok("FAQ.add.success");
    }

    @GetMapping("/frequently-questions/form-create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getFrequentlyQuestionsFormCreateByAdmin() {
        List<FrequentlyQuestionType> frequentlyQuestionTypes = villageInformationService.getAllFrequentlyQuestionType();

        Map<String, Object> response = Map.of(
                "frequentlyQuestionTypes", frequentlyQuestionTypes
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/frequently-questions/{questionId}/form-update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getFrequentlyQuestionsFormUpdateByAdmin(@PathVariable Long questionId) {
        List<FrequentlyQuestionType> frequentlyQuestionTypes = villageInformationService.getAllFrequentlyQuestionType();
        FrequentlyQuestion frequentlyQuestion = villageInformationService.findFrequentlyQuestionById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ.not.found"));

        Map<String, Object> response = Map.of(
                "frequentlyQuestionTypes", frequentlyQuestionTypes,
                "frequentlyQuestion", frequentlyQuestion
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/frequently-questions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateFrequentlyQuestionsByAdmin(@RequestBody FrequentlyQuestion frequentlyQuestion) {
        villageInformationService.updateFrequentlyQuestionsByAdmin(frequentlyQuestion);

        return ResponseEntity.ok("FAQ.update.success");
    }

    @DeleteMapping("/frequently-questions/{questionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteFrequentlyQuestionsByAdmin(@PathVariable Long questionId) {
        villageInformationService.deleteFrequentlyQuestionsByAdmin(questionId);

        return ResponseEntity.ok("FAQ.delete.success");
    }
}

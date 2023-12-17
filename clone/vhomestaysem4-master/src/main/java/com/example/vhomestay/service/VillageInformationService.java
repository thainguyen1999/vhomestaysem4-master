package com.example.vhomestay.service;

import com.example.vhomestay.enums.FrequentlyQuestionType;
import com.example.vhomestay.model.dto.request.question.FrequentlyQuestionInHomeResponseDto;
import com.example.vhomestay.model.dto.request.villageInformation.InformationAdminRequestDto;
import com.example.vhomestay.model.dto.response.ContactUsResponseDto;
import com.example.vhomestay.model.dto.response.VillageInformationResponseDto;
import com.example.vhomestay.model.entity.ContactUs;
import com.example.vhomestay.model.entity.FrequentlyQuestion;
import com.example.vhomestay.model.entity.VillageInformation;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface VillageInformationService extends BaseService<VillageInformation, Long> {
    Optional<VillageInformationResponseDto> getVillageInformation();

    List<ContactUs> getContactUs();

    void updateContactUsByAdmin(ContactUs contactUs);

    List<InformationAdminRequestDto> getVillageInformationByAdmin();

    void updateVillageInformationByAdmin(InformationAdminRequestDto informationAdminRequestDto) throws IOException;

    List<FrequentlyQuestion> getFrequentlyQuestionsByAdmin();

    void createFrequentlyQuestionsByAdmin(FrequentlyQuestion frequentlyQuestion);

    List<FrequentlyQuestionType> getAllFrequentlyQuestionType();

    Optional<FrequentlyQuestion> findFrequentlyQuestionById(Long questionId);

    void updateFrequentlyQuestionsByAdmin(FrequentlyQuestion frequentlyQuestion);

    List<FrequentlyQuestionInHomeResponseDto> getFrequentlyQuestions();

    void deleteFrequentlyQuestionsByAdmin(Long questionId);
}

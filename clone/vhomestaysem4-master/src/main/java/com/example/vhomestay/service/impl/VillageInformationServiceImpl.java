package com.example.vhomestay.service.impl;

import com.example.vhomestay.enums.BaseStatus;
import com.example.vhomestay.enums.FrequentlyQuestionType;
import com.example.vhomestay.enums.MediaType;
import com.example.vhomestay.enums.VillageInformationType;
import com.example.vhomestay.model.dto.request.question.FrequentlyQuestionInHomeResponseDto;
import com.example.vhomestay.model.dto.request.villageInformation.InformationAdminRequestDto;
import com.example.vhomestay.model.dto.response.VillageInformationResponseDto;
import com.example.vhomestay.model.dto.response.homestay.HomestayForSearchRoomManagerResponseDto;
import com.example.vhomestay.model.dto.response.household.HouseholdVillageInforResponseDto;
import com.example.vhomestay.model.entity.*;
import com.example.vhomestay.repository.*;
import com.example.vhomestay.service.StorageService;
import com.example.vhomestay.service.VillageInformationService;
import com.example.vhomestay.util.exception.ResourceInternalServerErrorException;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class VillageInformationServiceImpl extends BaseServiceImpl<VillageInformation, Long, VillageInformationRepository>
        implements VillageInformationService {
    private final VillageInformationRepository villageInformationRepository;
    private final CustomerTestimonialsRepository customerTestimonialsRepository;
    private final HouseholdRepository householdRepository;
    private final HomestayRepository homestayRepository;
    private final ContactUsRepository contactUsRepository;
    private final StorageService storageService;
    private final VillageMediaRepository villageMediaRepository;
    private final FrequentlyQuestionRepository frequentlyQuestionRepository;

    @Override
    public Optional<VillageInformationResponseDto> getVillageInformation() {
        VillageInformationResponseDto villageInformationResponseDto = new VillageInformationResponseDto();

        List<HouseholdVillageInforResponseDto> householdVillageInforResponseDtos = new ArrayList<>();
        HouseholdVillageInforResponseDto householdVillageInforResponseDto;

        List<Homestay> homestays;

        List<HomestayForSearchRoomManagerResponseDto> homestayDtos;
        HomestayForSearchRoomManagerResponseDto homestayDto;

        List<Household> households = householdRepository.findAllActive();
        List<VillageInformation> villageInformations = villageInformationRepository.findAll();
        List<CustomerTestimonials> customerTestimonials = customerTestimonialsRepository.findAll();
        Integer totalHousehold = householdRepository.getNumberOfHousehold();
        Integer totalHomestay = homestayRepository.findAllHomestays().size();

        for (Household h : households) {
            homestays = new ArrayList<>();
            homestayDtos = new ArrayList<>();
            householdVillageInforResponseDto = new HouseholdVillageInforResponseDto();
            householdVillageInforResponseDto.setId(h.getId());
            householdVillageInforResponseDto.setHouseholdName(h.getHouseholdName());

            log.info(h.getHouseholdName());

            homestays = homestayRepository.findAllActiveByHouseholdId(h.getId());
            for (Homestay hs : homestays
            ) {
                log.info(hs.getHomestayCode());
            }

            for (Homestay hs : homestays) {
                homestayDto = new HomestayForSearchRoomManagerResponseDto();
                homestayDto.setHomestayId(hs.getId());
                homestayDto.setHomestayCode(hs.getHomestayCode());

                homestayDtos.add(homestayDto);
            }

            householdVillageInforResponseDto.setHomestays(homestayDtos);

            householdVillageInforResponseDtos.add(householdVillageInforResponseDto);
        }

        villageInformationResponseDto.setVillageInformations(villageInformations);
        villageInformationResponseDto.setCustomerTestimonials(customerTestimonials);
        villageInformationResponseDto.setTotalHousehold(totalHousehold);
        villageInformationResponseDto.setTotalHomestay(totalHomestay);
        villageInformationResponseDto.setHouseholdResponseDtos(householdVillageInforResponseDtos);

        return Optional.of(villageInformationResponseDto);
    }

    @Override
    public List<ContactUs> getContactUs() {
        return contactUsRepository.findAll();
    }

    @Override
    public void updateContactUsByAdmin(ContactUs contactUs) {
        ContactUs c = contactUsRepository.findById(contactUs.getId())
                .orElseThrow(() -> new ResourceNotFoundException("contact.not.found"));

        c.setAddress(contactUs.getAddress());
        c.setPhone(contactUs.getPhone());
        c.setEmail(contactUs.getEmail());
        c.setLiveChat(contactUs.getLiveChat());

        try {
            contactUsRepository.save(c);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("contact.update.failed");
        }
    }

    @Override
    public List<InformationAdminRequestDto> getVillageInformationByAdmin() {
        List<VillageInformation> villageInformationList = villageInformationRepository.findAll();

        List<InformationAdminRequestDto> informationAdminRequestDtos = new ArrayList<>();
        InformationAdminRequestDto informationAdminRequestDto;
        for (VillageInformation v : villageInformationList) {
            informationAdminRequestDto = new InformationAdminRequestDto();

            informationAdminRequestDto.setId(v.getId());
            informationAdminRequestDto.setTitle(v.getTitle());
            informationAdminRequestDto.setDescription(v.getDescription());
            informationAdminRequestDto.setTotalVisitedCustomer(v.getTotalVisitedCustomer());
            informationAdminRequestDto.setTotalVisitor(v.getTotalVisitor());
            informationAdminRequestDto.setType(v.getType());
            informationAdminRequestDto.setOldImages(v.getImages());

            informationAdminRequestDtos.add(informationAdminRequestDto);
        }
        return informationAdminRequestDtos;
    }

    @Override
    public void updateVillageInformationByAdmin(InformationAdminRequestDto informationAdminRequestDto) throws IOException {
        VillageInformationType villageInformationType = informationAdminRequestDto.getType();

        VillageInformation v = villageInformationRepository.findById(informationAdminRequestDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("village.media.not.found"));

        if (!v.getType().equals(informationAdminRequestDto.getType())) {
            throw new ResourceInternalServerErrorException("village.media.update.failed");
        }

        v.setTitle(informationAdminRequestDto.getTitle());
        v.setDescription(informationAdminRequestDto.getDescription());

        List<VillageMedia> villageMedias = v.getImages();
        List<MultipartFile> newImages = informationAdminRequestDto.getNewImages();

        // Nếu là OUR_STORY, STAY_SERVICE thì phải update lại media -- update 1 ảnh
        if (villageInformationType.equals(VillageInformationType.OUR_STORY)
                || villageInformationType.equals(VillageInformationType.STAY_SERVICE)) {
            if (newImages != null) {
                int i = 0;
                int length = newImages.size();
                for (i = 0; i < length; i++) {
                    if (newImages.get(i) != null && !newImages.get(i).isEmpty()) {
                        villageMediaRepository.delete(villageMedias.get(0));
                        storageService.deleteFile(villageMedias.get(0).getFilePath());

                        String newFilePath = storageService.uploadFile(newImages.get(0));
                        List<VillageMedia> newVillageMedias = new ArrayList<>();

                        VillageMedia villageMedia = new VillageMedia();
                        villageMedia.setVillageInformation(v);
                        villageMedia.setFileName(newImages.get(0).getOriginalFilename());
                        villageMedia.setFilePath(newFilePath);
                        villageMedia.setType(MediaType.IMAGE);
                        villageMedias.add(villageMedia);

                        newVillageMedias.add(villageMedia);
                        v.setImages(newVillageMedias);

                        try {
                            villageMediaRepository.saveAll(newVillageMedias);
                        } catch (Exception e) {
                            throw new ResourceInternalServerErrorException("village.media.update.saveall.failed");
                        }
                        break;
                    }
                }
            }
            v.setTotalVisitedCustomer(informationAdminRequestDto.getTotalVisitedCustomer());
            v.setTotalVisitor(informationAdminRequestDto.getTotalVisitor());
//            if (newImages != null || !newImages.isEmpty()) {
//
//            }
//            else if (villageInformationType.equals(VillageInformationType.STAY_SERVICE)) {
//                v.setTotalVisitedCustomer(informationAdminRequestDto.getTotalVisitedCustomer());
//                v.setTotalVisitor(informationAdminRequestDto.getTotalVisitor());
//            }

        }

        // Nếu là CULTURE thì phải update lại media - update nhiều ảnh
        if (villageInformationType.equals(VillageInformationType.CULTURE)) {
            List<VillageMedia> villageMediasOldUpdate = informationAdminRequestDto.getOldImages();

            // Nếu có media cũ thì mới update
            if (villageMediasOldUpdate != null && !villageMediasOldUpdate.isEmpty()) {

                List<Long> oldUpdateIds = villageMediasOldUpdate.stream()
                        .map(VillageMedia::getId)
                        .collect(Collectors.toList());

                // Nếu số lượng media cũ khác với số lượng media mới thì update lại media
                if (villageMediasOldUpdate.size() != villageMedias.size()) {
                    // Xoá những media không nằm trong list media mới
                    for (VillageMedia vm : villageMedias) {
                        if (!oldUpdateIds.contains(vm.getId())) {
                            villageMediaRepository.delete(vm);
                            storageService.deleteFile(vm.getFilePath());
                        }
                    }

                    String newFilePath;
                    List<VillageMedia> newVillageMedias = new ArrayList<>();
                    VillageMedia villageMedia;

                    // Thêm những media mới vào
                    if (newImages != null) {
                        for (MultipartFile file : informationAdminRequestDto.getNewImages()) {
                            if (file != null && !file.isEmpty()) {
                                newFilePath = storageService.uploadFile(file);

                                villageMedia = new VillageMedia();
                                villageMedia.setFileName(file.getOriginalFilename());
                                villageMedia.setFilePath(newFilePath);
                                villageMedia.setType(MediaType.IMAGE);
                                villageMedia.setVillageInformation(v);

                                newVillageMedias.add(villageMedia);
                            }
                        }

                        v.setImages(newVillageMedias);
                        try {
                            villageMediaRepository.saveAll(newVillageMedias);
                        } catch (Exception e) {
                            throw new ResourceInternalServerErrorException("village.media.update.saveall.failed");
                        }
                    }
                }
            }
        }

        try {
            villageInformationRepository.save(v);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("village.information.update.failed");
        }

    }

    @Override
    public List<FrequentlyQuestion> getFrequentlyQuestionsByAdmin() {
        return frequentlyQuestionRepository.findAllActive();
    }

    @Override
    public void createFrequentlyQuestionsByAdmin(FrequentlyQuestion frequentlyQuestion) {
        FrequentlyQuestion fq = new FrequentlyQuestion();

        fq.setQuestion(frequentlyQuestion.getQuestion());
        fq.setAnswer(frequentlyQuestion.getAnswer());
        fq.setType(frequentlyQuestion.getType());
        fq.setStatus(BaseStatus.ACTIVE);

        try {
            frequentlyQuestionRepository.save(fq);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("FAQ.add.success");
        }
    }

    @Override
    public List<FrequentlyQuestionType> getAllFrequentlyQuestionType() {
        List<FrequentlyQuestionType> frequentlyQuestionTypes = new ArrayList<>();

        for (FrequentlyQuestionType fqt : FrequentlyQuestionType.values()) {
            frequentlyQuestionTypes.add(fqt);
        }
        return frequentlyQuestionTypes;
    }

    @Override
    public Optional<FrequentlyQuestion> findFrequentlyQuestionById(Long questionId) {
        FrequentlyQuestion frequentlyQuestion = frequentlyQuestionRepository.findByIdActive(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ.not.found"));
        return Optional.of(frequentlyQuestion);
    }

    @Override
    public void updateFrequentlyQuestionsByAdmin(FrequentlyQuestion frequentlyQuestion) {
        FrequentlyQuestion fq = frequentlyQuestionRepository.findByIdActive(frequentlyQuestion.getId())
                .orElseThrow(() -> new ResourceNotFoundException("FAQ.not.found"));

        fq.setQuestion(frequentlyQuestion.getQuestion());
        fq.setAnswer(frequentlyQuestion.getAnswer());
        fq.setType(frequentlyQuestion.getType());

        try {
            frequentlyQuestionRepository.save(fq);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("FAQ.update.failed");
        }
    }

    @Override
    public List<FrequentlyQuestionInHomeResponseDto> getFrequentlyQuestions() {
        List<FrequentlyQuestionType> frequentlyQuestionTypes = new ArrayList<>();
        frequentlyQuestionTypes = getAllFrequentlyQuestionType();

        List<FrequentlyQuestionInHomeResponseDto> frequentlyQuestionInHomeResponseDtos = new ArrayList<>();
        FrequentlyQuestionInHomeResponseDto frequentlyQuestionInHomeResponseDto;

        List<FrequentlyQuestion> frequentlyQuestions;

        for (FrequentlyQuestionType fqt : frequentlyQuestionTypes) {
            frequentlyQuestionInHomeResponseDto = new FrequentlyQuestionInHomeResponseDto();

            frequentlyQuestions = frequentlyQuestionRepository.findAllActiveAndType(fqt);

            frequentlyQuestionInHomeResponseDto.setType(fqt);
            frequentlyQuestionInHomeResponseDto.setFrequentlyQuestions(frequentlyQuestions);

            frequentlyQuestionInHomeResponseDtos.add(frequentlyQuestionInHomeResponseDto);
        }

        return frequentlyQuestionInHomeResponseDtos;
    }

    @Override
    public void deleteFrequentlyQuestionsByAdmin(Long questionId) {
        FrequentlyQuestion fq = frequentlyQuestionRepository.findByIdActive(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ.not.found"));

        fq.setStatus(BaseStatus.DELETED);

        try {
            frequentlyQuestionRepository.save(fq);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("FAQ.delete.failed");
        }
    }
}

package com.example.vhomestay.service.impl;

import com.example.vhomestay.enums.HouseholdStatus;
import com.example.vhomestay.model.dto.request.household.HouseholdInfoRequestDto;
import com.example.vhomestay.model.dto.request.household.HouseholdMediaRequestDto;
import com.example.vhomestay.model.dto.request.household.HouseholdTOPRequestDto;
import com.example.vhomestay.model.dto.response.booking.customer.HouseholdNameDto;
import com.example.vhomestay.model.dto.response.homestay.HomestayDetailForCustomerResponse;
import com.example.vhomestay.model.dto.response.household.admin.HomestayDetailForAdminResponse;
import com.example.vhomestay.model.dto.response.household.admin.HouseholdDetailForAdminResponse;
import com.example.vhomestay.model.dto.response.household.admin.HouseholdInfoTopResponseDto;
import com.example.vhomestay.model.dto.response.household.admin.HouseholdResponseDto;
import com.example.vhomestay.model.dto.response.household.customer.HouseholdDetailForCustomer;
import com.example.vhomestay.model.dto.response.household.customer.HouseholdInTopDto;
import com.example.vhomestay.model.dto.response.roomtype.customer.HouseholdRoomTypeForCustomerResponse;
import com.example.vhomestay.model.dto.response.service.manager.ServiceDetailResponse;
import com.example.vhomestay.model.dto.response.user.HouseholdInfoResponseDto;
import com.example.vhomestay.model.entity.Homestay;
import com.example.vhomestay.model.entity.HomestayMedia;
import com.example.vhomestay.model.entity.Household;
import com.example.vhomestay.model.entity.Room;
import com.example.vhomestay.repository.*;
import com.example.vhomestay.security.SecurityUtil;
import com.example.vhomestay.service.HouseholdService;
import com.example.vhomestay.service.StorageService;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import com.example.vhomestay.util.exception.ResourceInternalServerErrorException;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class HouseholdServiceImpl
        extends BaseServiceImpl<Household, Long, HouseholdRepository>
        implements HouseholdService {
    private final HouseholdRepository householdRepository;
    private final HomestayRepository homestayRepository;
    private final HomestayMediaRepository homestayMediaRepository;
    private final HouseholdRoomTypeRepository householdRoomTypeRepository;
    private final ServiceRepository serviceRepository;
    private final RoomRepository roomRepository;
    private final FeedbackRepository feedbackRepository;
    private final ModelMapper modelMapper;
    private final StorageService storageService;

    public HouseholdServiceImpl(HouseholdRepository householdRepository, HomestayRepository homestayRepository, HomestayMediaRepository homestayMediaRepository, HouseholdRoomTypeRepository householdRoomTypeRepository, ServiceRepository serviceRepository, RoomRepository roomRepository, FeedbackRepository feedbackRepository, ModelMapper modelMapper, StorageService storageService) {
        this.householdRepository = householdRepository;
        this.homestayRepository = homestayRepository;
        this.homestayMediaRepository = homestayMediaRepository;
        this.householdRoomTypeRepository = householdRoomTypeRepository;
        this.serviceRepository = serviceRepository;
        this.roomRepository = roomRepository;
        this.feedbackRepository = feedbackRepository;
        this.modelMapper = modelMapper;
        this.storageService = storageService;
        configureModelMapper();
    }

    @Override
    public List<Household> getAllHousehold() {
        return householdRepository.getAllHouseholdNotDelete();
    }
    @Override
    public Optional<Household> getHouseholdByManagerEmail() {
        // GET CURRENT USER EMAIL
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        String managerEmail = currentUserLoginEmailOptional.get();

        Optional<Household> householdOptional = householdRepository.findByManagerEmail(managerEmail);

        return householdOptional;
    }

    @Override
    public HouseholdDetailForCustomer getHouseholdDetailForCustomer(Long householdId) {
        HouseholdDetailForCustomer householdDetailForCustomer = householdRepository.getHouseholdDetailForCustomer(householdId);
        if (householdDetailForCustomer == null) {
            throw new ResourceNotFoundException("household.not.found");
        }
        List<HomestayMedia> homestayMediaList = new ArrayList<>();
        List<HomestayDetailForCustomerResponse> homestayDetailForCustomerResponseList = homestayRepository.getHomestayListForCustomer(householdId);
        for (HomestayDetailForCustomerResponse homestayDetailForCustomerResponse : homestayDetailForCustomerResponseList){
            homestayMediaList = homestayMediaRepository.findAllByHomestayId(homestayDetailForCustomerResponse.getHomestayId());
            homestayDetailForCustomerResponse.setMedias(homestayMediaList);
        }

        List<HouseholdRoomTypeForCustomerResponse> householdRoomTypeForCustomerResponseList = householdRoomTypeRepository.getRoomTypeListForCustomer(householdId);
        List<ServiceDetailResponse> serviceDetailResponseList = serviceRepository.getServicesByHouseholdId(householdId);
        householdDetailForCustomer.setHomestayDetailForCustomerList(homestayDetailForCustomerResponseList);
        householdDetailForCustomer.setHouseholdRoomTypeForCustomerList(householdRoomTypeForCustomerResponseList);
        householdDetailForCustomer.setServiceDetailForCustomerList(serviceDetailResponseList);
        return householdDetailForCustomer;
    }

    @Override
    public Optional<Household> getHouseholdByManagerEmail(String email) {
        return householdRepository.findByManagerEmail(email);
    }

    private void configureModelMapper() {
        modelMapper.createTypeMap(Household.class, HouseholdResponseDto.class)
                .addMapping(Household::getId, HouseholdResponseDto::setId)
                .addMapping(Household::getHouseholdName, HouseholdResponseDto::setHouseholdName)
                .addMapping(Household::getCheckInTime, HouseholdResponseDto::setCheckInTime)
                .addMapping(Household::getCheckOutTime, HouseholdResponseDto::setCheckOutTime)
                .addMapping(Household::getCancellationPeriod, HouseholdResponseDto::setCancellationPeriod)
                .addMapping(Household::getPhoneNumberFirst, HouseholdResponseDto::setPhoneNumberFirst)
                .addMapping(Household::getPhoneNumberSecond, HouseholdResponseDto::setPhoneNumberSecond)
                .addMapping(Household::getEmail, HouseholdResponseDto::setEmail)
                .addMapping(Household::getLinkFacebook, HouseholdResponseDto::setLinkFacebook)
                .addMapping(Household::getLinkTiktok, HouseholdResponseDto::setLinkTiktok)
                .addMapping(Household::getDescription, HouseholdResponseDto::setDescription)
                .addMapping(Household::getAvatar, HouseholdResponseDto::setAvatar)
                .addMapping(Household::getCoverImage, HouseholdResponseDto::setCoverImage)
                .addMapping(Household::getLinkYoutube, HouseholdResponseDto::setLinkYoutube);
    }

    @Override
    public HouseholdResponseDto mapToDTO(Household household) {
        return modelMapper.map(household, HouseholdResponseDto.class);
    }

    @Override
    public Household mapToEntity(HouseholdResponseDto householdResponseDto) {
        return modelMapper.map(householdResponseDto, Household.class);
    }

    @Override
    public boolean updateHouseholdInformation(HouseholdInfoRequestDto householdInfoRequestDto) {
        Optional<Household> householdOptional = householdRepository.findById(householdInfoRequestDto.getId());

        if (householdOptional.isEmpty()) {
            throw new ResourceNotFoundException("household.not.found");
        }

        Household household = householdOptional.get();
        household.setId(householdInfoRequestDto.getId());

        if(householdInfoRequestDto.getHouseholdName() != null) {
            household.setHouseholdName(householdInfoRequestDto.getHouseholdName());
        }

        if(householdInfoRequestDto.getPhoneNumberFirst() != null) {
            household.setPhoneNumberFirst(householdInfoRequestDto.getPhoneNumberFirst());
        }

        if(householdInfoRequestDto.getPhoneNumberSecond() != null) {
            household.setPhoneNumberSecond(householdInfoRequestDto.getPhoneNumberSecond());
        }

        if(householdInfoRequestDto.getEmail() != null) {
            household.setEmail(householdInfoRequestDto.getEmail());
        }

        if(householdInfoRequestDto.getLinkFacebook() != null) {
            household.setLinkFacebook(householdInfoRequestDto.getLinkFacebook());
        }

        if(householdInfoRequestDto.getLinkTiktok() != null) {
            household.setLinkTiktok(householdInfoRequestDto.getLinkTiktok());
        }

        if(householdInfoRequestDto.getDescription() != null) {
            household.setDescription(householdInfoRequestDto.getDescription());
        }

        if(householdInfoRequestDto.getLinkYoutube() != null) {
            household.setLinkYoutube(householdInfoRequestDto.getLinkYoutube());
        }

        if(householdInfoRequestDto.getCheckInTime() != null) {
            household.setCheckInTime(householdInfoRequestDto.getCheckInTime());
        }

        if(householdInfoRequestDto.getCheckOutTime() != null) {
            household.setCheckOutTime(householdInfoRequestDto.getCheckOutTime());
        }

        if(householdInfoRequestDto.getCancellationPeriod() != null) {
            household.setCancellationPeriod(householdInfoRequestDto.getCancellationPeriod());
        }

        try {
            householdRepository.save(household);
            return true;
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public boolean updateHouseholdMedia(HouseholdMediaRequestDto householdMediaRequestDto) {
        Optional<Household> householdOptional = getHouseholdByManagerEmail();

        Household h = householdOptional.get();

        if (householdMediaRequestDto.getAvatar() != null && !householdMediaRequestDto.getAvatar().isEmpty()) {
            String oldAvatar = h.getAvatar();
            try {
                String newAvatar = storageService.updateFile(oldAvatar, householdMediaRequestDto.getAvatar());
                h.setAvatar(newAvatar);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (householdMediaRequestDto.getCoverImage() != null && !householdMediaRequestDto.getCoverImage().isEmpty()) {
            String oldCoverImage = h.getCoverImage();
            try {
                String newCoverImage = storageService.updateFile(oldCoverImage, householdMediaRequestDto.getCoverImage());
                h.setCoverImage(newCoverImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if(householdMediaRequestDto.getAvatar() == null
                && householdMediaRequestDto.getCoverImage() == null){
            throw new ResourceBadRequestException("bad.request");
        }

        try {
            householdRepository.save(h);
            return true;
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public List<HouseholdNameDto> findAllHouseholdName() {
        return householdRepository.findAllHouseholdName();
    }

    @Override
    public HouseholdDetailForAdminResponse getHouseholdDetailForAdmin(Long householdId) {
        Household household = householdRepository.findById(householdId).get();
        HouseholdDetailForAdminResponse householdDetailForAdminResponses = new HouseholdDetailForAdminResponse();
        if (household.getManager() != null){
            householdDetailForAdminResponses.setManagerFirstName(household.getManager().getFirstName());
            householdDetailForAdminResponses.setManagerLastName(household.getManager().getLastName());
            householdDetailForAdminResponses.setManagerPhone(household.getManager().getPhoneNumber());
            householdDetailForAdminResponses.setManagerEmail(household.getManager().getAccount().getEmail());
        } else {
            householdDetailForAdminResponses.setManagerFirstName("Không có quản lí");
            householdDetailForAdminResponses.setManagerLastName("");
            householdDetailForAdminResponses.setManagerPhone("");
            householdDetailForAdminResponses.setManagerEmail("");
        }
        householdDetailForAdminResponses.setHouseholdId(householdId);
        householdDetailForAdminResponses.setHouseholdName(household.getHouseholdName());
        householdDetailForAdminResponses.setAvatar(household.getAvatar());
        householdDetailForAdminResponses.setCoverImage(household.getCoverImage());
        householdDetailForAdminResponses.setPhoneNumberFirst(household.getPhoneNumberFirst());
        householdDetailForAdminResponses.setPhoneNumberSecond(household.getPhoneNumberSecond());
        householdDetailForAdminResponses.setEmail(household.getEmail());
        householdDetailForAdminResponses.setLinkFacebook(household.getLinkFacebook());
        householdDetailForAdminResponses.setLinkTiktok(household.getLinkTiktok());
        householdDetailForAdminResponses.setDescription(household.getDescription());
        householdDetailForAdminResponses.setLinkYoutube(household.getLinkYoutube());
        householdDetailForAdminResponses.setHouseholdStatus(household.getStatus());
        List<Homestay> homestays = homestayRepository.findAllHomestayByHouseholdId(household.getId());
        List<HomestayDetailForAdminResponse> homestayDetailForAdminResponses = new ArrayList<>();
        for (Homestay homestay : homestays) {
            HomestayDetailForAdminResponse homestayDetailForAdminResponse = new HomestayDetailForAdminResponse();
            homestayDetailForAdminResponse.setHomestayId(homestay.getId());
            homestayDetailForAdminResponse.setHomestayCode(homestay.getHomestayCode());
            homestayDetailForAdminResponse.setHomestayStatus(homestay.getStatus());
            int totalCapacity = 0, numberOfRoom = 0 , numberOfDorm = 0;
            List<Room> rooms = homestay.getRooms();
            for (Room room : rooms) {
                if (!room.getHouseholdRoomType().getRoomType().getIsDorm()){
                    numberOfRoom++;
                    totalCapacity += room.getHouseholdRoomType().getCapacity();
                } else {
                    numberOfDorm++;
                    totalCapacity += roomRepository.countDormSlotByRoomId(room.getId());
                }
            }
            homestayDetailForAdminResponse.setNumberOfRoom(numberOfRoom);
            homestayDetailForAdminResponse.setNumberOfDorm(numberOfDorm);
            homestayDetailForAdminResponse.setTotalCapacity(totalCapacity);
            homestayDetailForAdminResponses.add(homestayDetailForAdminResponse);
        }
        householdDetailForAdminResponses.setNumberOfHomestay(homestays.size());
        householdDetailForAdminResponses.setHomestayDetailForAdminList(homestayDetailForAdminResponses);

        return householdDetailForAdminResponses;
    }

    @Override
    public boolean uploadHouseholdAvatar(MultipartFile image, String householdName) throws IOException {
        Household household = new Household();

        if(image != null && !image.isEmpty()) {
            String newAvatar = storageService.uploadFile(image);
            household.setAvatar(newAvatar);
        }
        household.setHouseholdName(householdName);
        household.setStatus(HouseholdStatus.INACTIVE);
        householdRepository.save(household);
        return true;
    }

    @Override
    public boolean updateHouseholdAvatar(MultipartFile image, String householdName , Long householdId) throws IOException {
        Optional<Household> householdOptional = householdRepository.findById(householdId);
        if (householdOptional.isEmpty()) {
            throw new ResourceNotFoundException("household.not.found");
        }
        Household household = householdOptional.get();
        String newAvatar = "", oldAvatar = "";
        if (image != null && !image.isEmpty()){
            oldAvatar = household.getAvatar();
            newAvatar = storageService.updateFile(oldAvatar, image);
            household.setAvatar(newAvatar);
        }
        if (householdName != null){
            household.setHouseholdName(householdName);
        }
        try {
            householdRepository.save(household);
            return true;
        } catch (Exception e) {
            storageService.deleteFile(newAvatar);
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public String deleteHouseholdAvatar(Long householdId) {
        Optional<Household> householdOptional = householdRepository.findById(householdId);
        if (householdOptional.isEmpty()) {
            throw new ResourceNotFoundException("household.not.found");
        }
        Household household = householdOptional.get();
        String oldAvatar = household.getAvatar();
        storageService.deleteFile(oldAvatar);
        household.setAvatar("avatar.default");
        try {
            householdRepository.save(household);
            return household.getAvatar();
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public boolean isHouseholdNameExist(String householdName) {
        Optional<Household> householdOptional = householdRepository.findByHouseholdName(householdName);
        return householdOptional.isPresent();
    }

    @Override
    public boolean HideOrShowHousehold(Long householdId) {
        Optional<Household> householdOptional = householdRepository.findById(householdId);
        if (householdOptional.isEmpty()) {
            throw new ResourceNotFoundException("household.not.found");
        }
        Household household = householdOptional.get();
        if (household.getStatus() == HouseholdStatus.ACTIVE) {
            household.setStatus(HouseholdStatus.INACTIVE);
        } else if (household.getStatus() == HouseholdStatus.INACTIVE) {
            household.setStatus(HouseholdStatus.ACTIVE);
        }
        try {
            householdRepository.save(household);
            return true;
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public boolean deleteHousehold(Long householdId) {
        Optional<Household> householdOptional = householdRepository.findById(householdId);
        if (householdOptional.isEmpty()) {
            throw new ResourceNotFoundException("household.not.found");
        }
        Household household = householdOptional.get();
        household.setStatus(HouseholdStatus.DELETED);
        try {
            householdRepository.save(household);
            return true;
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public HouseholdInfoResponseDto getHouseholdInfoByAdmin(Long accountId) {
        Optional<Household> household = householdRepository.findByAccountManagerId(accountId);
        if (household.isEmpty()) {
            return null;
        }
        HouseholdInfoResponseDto householdInfoResponseDto = new HouseholdInfoResponseDto();
        householdInfoResponseDto.setHouseholdName(household.get().getHouseholdName());
        householdInfoResponseDto.setHouseholdAvatar(household.get().getAvatar());
        householdInfoResponseDto.setHouseholdEmail(household.get().getEmail());
        householdInfoResponseDto.setHouseholdStatus(household.get().getStatus());
        householdInfoResponseDto.setHouseholdPhone1(household.get().getPhoneNumberFirst());
        householdInfoResponseDto.setHouseholdPhone2(household.get().getPhoneNumberSecond());
        householdInfoResponseDto.setHomestayCode(homestayRepository.getAllHomestayCodeByHouseholdId(household.get().getId()));
        return householdInfoResponseDto;
    }

    @Override
    public List<HouseholdInfoTopResponseDto> getTopHousehold() {
        List<HouseholdInfoTopResponseDto> householdList = householdRepository.getTopHousehold();
        for (HouseholdInfoTopResponseDto household : householdList) {
            List<String> homestayCode = homestayRepository.getAllHomestayCodeByHouseholdId(household.getId());
            String code = String.join(", ", homestayCode);
            household.setHomestay(code);
        }
        return householdList;
    }

    @Override
    public List<HouseholdNameDto> getHouseholdNotInTop() {
        return householdRepository.getHouseholdNotInTop();
    }

    @Override
    public void setTopHousehold(HouseholdTOPRequestDto householdTOPRequestDto) {
        Household household = householdRepository.findById(householdTOPRequestDto.getHouseholdId()).orElseThrow(() -> new ResourceNotFoundException("household.not.found"));
        Household householdTop = householdRepository.findByHouseHoldTOP(householdTOPRequestDto.getTop()).orElse(null);
        if (householdTop != null) {
            throw new ResourceBadRequestException("household.top.exist");
        }

        try {
            household.setTop(householdTOPRequestDto.getTop());
            householdRepository.save(household);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }

    }

    @Override
    public void deleteTopHousehold(int top) {
        Household householdRemoveTop = householdRepository.findByHouseHoldTOP(top).orElseThrow(() -> new ResourceNotFoundException("household.not.found"));
        try {
            householdRemoveTop.setTop(null);
            householdRepository.save(householdRemoveTop);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public List<HouseholdInTopDto> findAllHouseholdInTop() {
        List<HouseholdInTopDto> householdInTopDtoList = householdRepository.getTopHouseholdForHomePage();
        for (HouseholdInTopDto household : householdInTopDtoList) {
            List<String> homestayCode = homestayRepository.getAllHomestayCodeByHouseholdId(household.getId());
            Double rating = feedbackRepository.getRatingByHouseholdId(household.getId());
            String code = String.join(" - ", homestayCode);
            household.setHomestay(code);
            household.setRateAverage(rating == null ? 0 : rating);
        }
        return householdInTopDtoList;
    }

    @Override
    public List<Integer> getTopList() {
        int[] top = new int[]{1, 2, 3, 4, 5};
        List<Integer> topList = Arrays.stream(top).boxed().collect(Collectors.toList());
        List<Integer> topListInSystem = householdRepository.getTopList();
        topList.removeAll(topListInSystem);
        return topList;
    }

}

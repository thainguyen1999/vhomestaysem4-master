package com.example.vhomestay.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.vhomestay.enums.HouseholdTypeRoomStatus;
import com.example.vhomestay.enums.MediaType;
import com.example.vhomestay.enums.RequestStatus;
import com.example.vhomestay.enums.RequestType;
import com.example.vhomestay.model.dto.response.*;
import com.example.vhomestay.model.entity.*;
import com.example.vhomestay.repository.*;
import com.example.vhomestay.service.FacilityService;
import com.example.vhomestay.service.HouseholdRoomTypeService;
import com.example.vhomestay.service.RoomTypeService;
import com.example.vhomestay.service.StorageService;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import com.example.vhomestay.util.validation.Validation;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HouseholdRoomTypeServiceImpl extends BaseServiceImpl<HouseholdRoomType, Long, HouseholdRoomTypeRepository>
    implements HouseholdRoomTypeService {
    private final HouseholdRoomTypeRepository householdRoomTypeRepository;
    private final HouseholdRepository householdRepository;
    private final RoomTypeFacilityRepository roomTypeFacilityRepository;
    private final StorageService storageService;
    private final HomestayMediaRepository homestayMediaRepository;
    private final RoomTypeService roomTypeService;
    private final FacilityService facilityService;
    private final RequestRepository requestRepository;
    private final ModelMapper modelMapper;


    public HouseholdRoomTypeServiceImpl(HouseholdRoomTypeRepository householdRoomTypeRepository, HouseholdRepository householdRepository, RoomTypeFacilityRepository roomTypeFacilityRepository, StorageService storageService, HomestayMediaRepository homestayMediaRepository, RoomTypeService roomTypeService, FacilityService facilityService, RequestRepository requestRepository, ModelMapper modelMapper) {
        this.householdRoomTypeRepository = householdRoomTypeRepository;
        this.householdRepository = householdRepository;
        this.roomTypeFacilityRepository = roomTypeFacilityRepository;
        this.storageService = storageService;
        this.homestayMediaRepository = homestayMediaRepository;
        this.roomTypeService = roomTypeService;
        this.facilityService = facilityService;
        this.requestRepository = requestRepository;
        this.modelMapper = modelMapper;
        configureModelMapper();
    }

    private void configureModelMapper() {
        modelMapper.createTypeMap(HouseholdRoomType.class , HouseholdRoomTypeResponseDto.class)
                .addMapping(HouseholdRoomType::getId, HouseholdRoomTypeResponseDto::setHouseholdRoomTypeId)
                .addMapping(HouseholdRoomType::getCapacity, HouseholdRoomTypeResponseDto::setCapacity)
                .addMapping(src -> src.getRoomType().getDoubleBed(), HouseholdRoomTypeResponseDto::setDoubleBed)
                .addMapping(src -> src.getRoomType().getSingleBed(), HouseholdRoomTypeResponseDto::setSingleBed)
                .addMapping(HouseholdRoomType::getPrice, HouseholdRoomTypeResponseDto::setPrice)
                .addMapping(HouseholdRoomType::getPriceUpdate, HouseholdRoomTypeResponseDto::setPriceUpdate)
                .addMapping(src -> src.getRoomType().getRoomTypeName(), HouseholdRoomTypeResponseDto::setRoomTypeName)
                .addMapping(HouseholdRoomType::getIsChildrenAndBed, HouseholdRoomTypeResponseDto::setIsChildrenAndBed)
                .addMapping(HouseholdRoomType::getRoomTypeFacilities, HouseholdRoomTypeResponseDto::setFacilities)
                .addMapping(HouseholdRoomType::getHomestayMedias, HouseholdRoomTypeResponseDto::setHomestayMedias)
                .addMapping(HouseholdRoomType::getStatus, HouseholdRoomTypeResponseDto::setStatus);
    }


    @Override
    public boolean addHouseholdRoomType(HouseholdRoomTypeCreateFormResponseDto householdRoomTypeCreateFormResponseDto, Long id) throws IOException {
        Optional<Household> householdOptional = householdRepository.findById(id);
        //Save household room type to database
        RoomTypeDto roomType = householdRoomTypeCreateFormResponseDto.getRoomTypes().get(0);
        RoomType roomTypeEntity = roomTypeService.mapDtoToRoomType(roomType);
        HouseholdRoomType householdRoomType = new HouseholdRoomType();
        householdRoomType.setHousehold(householdOptional.orElseThrow(() -> new ResourceNotFoundException("household.not.found")));
        householdRoomType.setRoomType(roomTypeEntity);
        householdRoomType.setCapacity(householdRoomTypeCreateFormResponseDto.getCapacity());
        householdRoomType.setIsChildrenAndBed(householdRoomTypeCreateFormResponseDto.getIsChildrenAndBed());
        householdRoomType.setPriceUpdate(householdRoomTypeCreateFormResponseDto.getPriceUpdate());
        householdRoomType.setPrice(BigDecimal.valueOf(0));
        householdRoomType.setStatus(HouseholdTypeRoomStatus.ACTIVE_PRICE_SET_FIRST_TIME);
        householdRoomTypeRepository.save(householdRoomType);
        HouseholdRoomType householdRt = householdRoomTypeRepository.findByHouseholdIdAndRoomTypeId(id, householdRoomTypeCreateFormResponseDto.getRoomTypes().get(0).getId()).orElseThrow(() -> new ResourceNotFoundException("Household Room Type not found"));
        createRequestPriceTypeRoom(id, householdRt.getId(), householdRoomTypeCreateFormResponseDto.getPriceUpdate(), HouseholdTypeRoomStatus.ACTIVE_PRICE_SET_FIRST_TIME);

        //Save Household Room Type Facility to database

        List<FacilityDto> facilities = householdRoomTypeCreateFormResponseDto.getFacilities();
        List<Facility> facilityList = facilityService.mapDtoListToFacilityList(facilities);
        facilityList.forEach(facility -> {
            RoomTypeFacility roomTypeFacility = new RoomTypeFacility();
            roomTypeFacility.setFacility(facility);
            roomTypeFacility.setHouseholdRoomType(householdRt);
            roomTypeFacilityRepository.save(roomTypeFacility);
        });

        //Save Household Room Type Media to database
        List<MultipartFile> images = householdRoomTypeCreateFormResponseDto.getMediaFiles();
        if (images != null && !images.isEmpty()) {
            addImagesToRoomType(householdRt, images);
        }
        return true;
    }

    private void addImagesToRoomType(HouseholdRoomType householdRoomType, List<MultipartFile> images) throws IOException {
        if (images == null || images.isEmpty()) {
            return;
        }
        for (MultipartFile image : images) {
            String imageName = image.getOriginalFilename();
            String imageUrl = storageService.uploadFile(image);
            saveImageToDatabase(householdRoomType, imageName, imageUrl);
        }
    }

    private void saveImageToDatabase(HouseholdRoomType householdRoomType, String imageName, String imageUrl) {
        HomestayMedia homestayMedia = new HomestayMedia();
        homestayMedia.setHouseholdRoomType(householdRoomType);
        homestayMedia.setFileName(imageName);
        homestayMedia.setFilePath(imageUrl);
        homestayMedia.setType(MediaType.IMAGE);
        homestayMediaRepository.save(homestayMedia);
    }

    @Override
    public Optional<HouseholdRoomType> findByHouseholdIdAndRoomTypeId(Long householdId, Long roomTypeId) {
        return householdRoomTypeRepository.findByHouseholdIdAndRoomTypeId(householdId, roomTypeId);
    }

    @Override
    public List<HouseholdRoomTypeDto> findHouseholdRoomTypeByHouseholdId(Long householdId) {
        List<HouseholdRoomTypeDto> householdRoomTypeDtoList = new ArrayList<>();
        List<Long> householdRoomTypeId = householdRoomTypeRepository.findIdNotDormByHouseholdId(householdId);
        for (Long id : householdRoomTypeId) {
            HouseholdRoomTypeDto householdRoomTypeDto = new HouseholdRoomTypeDto();
            Optional<HouseholdRoomType> householdRoomTypeOptional = householdRoomTypeRepository.findById(id);
            HouseholdRoomType householdRoomType = householdRoomTypeOptional.orElseThrow(() -> new ResourceNotFoundException("Household room type not found"));
            householdRoomTypeDto.setId(householdRoomType.getId());
            householdRoomTypeDto.setCapacity(householdRoomType.getCapacity());
            householdRoomTypeDto.setIsChildrenAndBed(householdRoomType.getIsChildrenAndBed());
            householdRoomTypeDto.setPrice(householdRoomType.getPrice());
            householdRoomTypeDto.setRoomTypeName(householdRoomType.getRoomType().getRoomTypeName());
            householdRoomTypeDto.setSingleBed(householdRoomType.getRoomType().getSingleBed());
            householdRoomTypeDto.setDoubleBed(householdRoomType.getRoomType().getDoubleBed());
            List<Facility> facilities = roomTypeFacilityRepository.findFacilitiesByHouseholdRoomTypeId(householdRoomType.getId());
            householdRoomTypeDto.setFacilities(facilityService.mapFacilityListToDtoList(facilities));
            householdRoomTypeDtoList.add(householdRoomTypeDto);
        }
        return householdRoomTypeDtoList;
    }

    @Override
    public List<HouseholdRoomTypeResponseDto> getAllHouseholdRoomTypeThatManageIsManaged(String managerEmail) {
        List<HouseholdRoomType> householdRoomTypes = householdRoomTypeRepository.getAllHouseholdRoomTypeByManagerEmail(managerEmail);
        List<HouseholdRoomTypeResponseDto> householdRoomTypeDtoList = new ArrayList<>();
        HouseholdRoomTypeResponseDto householdRoomTypeResponseDto;
        for (HouseholdRoomType householdRoomType : householdRoomTypes) {
            householdRoomTypeResponseDto = mapToDTO(householdRoomType);
            householdRoomTypeResponseDto.setHouseholdRoomTypeId(householdRoomType.getId());
            householdRoomTypeDtoList.add(householdRoomTypeResponseDto);
        }
        return householdRoomTypeDtoList;
    }

    @Override
    public HouseholdRoomTypeResponseDto mapToDTO(HouseholdRoomType householdRoomType) {
        return modelMapper.map(householdRoomType, HouseholdRoomTypeResponseDto.class);
    }

    @Override
    public HouseholdRoomType mapToEntity(HouseholdRoomTypeResponseDto householdRoomTypeResponseDto) {
        return modelMapper.map(householdRoomTypeResponseDto, HouseholdRoomType.class);
    }

    @Override
    public HouseholdRoomTypeResponseDto showHouseholdRoomTypeForEdit(Long householdRoomTypeId, Household household) {
        Optional<HouseholdRoomType> optionalHouseholdRoomType = householdRoomTypeRepository.findById(householdRoomTypeId);

        if (optionalHouseholdRoomType.isEmpty()) {
            throw new ResourceNotFoundException("household.room.type.not.found");
        }
        HouseholdRoomTypeResponseDto householdRoomTypeResponseDto;

        if (optionalHouseholdRoomType.get().getHousehold().equals(household)) {
            householdRoomTypeResponseDto = mapToDTO(optionalHouseholdRoomType.get());
        } else {
            throw new ResourceBadRequestException("no.permission");
        }
        return householdRoomTypeResponseDto;
    }

    @Override
    public boolean createRequestPriceTypeRoom(Long householdId, Long householdRoomTypeId, BigDecimal price, HouseholdTypeRoomStatus status) {
        Optional<RoomPriceUpdateDto> householdRoomTypeOptional = householdRoomTypeRepository.findHouseholdRoomTypeDetailById(householdRoomTypeId);
        if (householdRoomTypeOptional.isEmpty()) {
            throw new ResourceNotFoundException("household.room.type.not.found");
        }
        Household household = householdRepository.findById(householdId).orElseThrow(() -> new ResourceNotFoundException("Household not found"));
        ObjectMapper objectMapper = new ObjectMapper();
        RoomPriceUpdateDto roomPriceUpdateDto = householdRoomTypeOptional.get();
        roomPriceUpdateDto.setPriceUpdate(price);
        try {
            String householdRoomTypeJson = objectMapper.writeValueAsString(roomPriceUpdateDto);
            Request request = new Request();
            if (status.equals(HouseholdTypeRoomStatus.ACTIVE_PRICE_SET_FIRST_TIME)) {
                request.setRequestTitle("Tạo mới giá phòng");
                request.setRequestContent("Đăng giá phòng '" + roomPriceUpdateDto.getRoomTypeName() + "' với giá " + Validation.validateDecimalNumber(price) + " VNĐ");
                request.setRequestType(RequestType.CREATE_ROOM);
            } else {
                request.setRequestTitle("Cập nhật giá phòng");
                request.setRequestContent("Thay đổi giá phòng '" + roomPriceUpdateDto.getRoomTypeName() + "' từ " + Validation.validateDecimalNumber(roomPriceUpdateDto.getPrice()) + " VNĐ sang " + Validation.validateDecimalNumber(price) + " VNĐ");
                request.setRequestType(RequestType.UPDATE_ROOM);
            }
            request.setRequestData(householdRoomTypeJson);
            request.setRequestStatus(RequestStatus.PENDING);
            request.setHousehold(household);
            requestRepository.save(request);
        } catch (JsonProcessingException e) {
            throw new ResourceBadRequestException("household.room.type.convert.failed");
        }
        return true;
    }

    @Override
    public boolean editHouseholdRoomType(Long id, HouseholdRoomTypeResponseDto householdRoomTypeResponseDto) throws IOException {
        Optional<HouseholdRoomType> householdRoomTypeOptional = householdRoomTypeRepository.findById(id);
        if (householdRoomTypeOptional.isEmpty()) {
            throw new ResourceNotFoundException("household.room.type.not.found");
        }
        HouseholdRoomType householdRoomType = householdRoomTypeOptional.get();
        Long householdId = householdRoomType.getHousehold().getId();
        BigDecimal oldPrice = householdRoomType.getPrice();
        BigDecimal newPrice = householdRoomTypeResponseDto.getPriceUpdate();

        //Delete old Media
        List<HomestayMedia> homestayMediaListOld = householdRoomType.getHomestayMedias();
        List<HomestayMedia> homestayMediaListNew = householdRoomTypeResponseDto.getHomestayMedias();

        if(homestayMediaListNew != null){
            for (HomestayMedia media : homestayMediaListNew) {
                media.setHouseholdRoomType(householdRoomType);
            }
            for (HomestayMedia media : homestayMediaListOld) {
                if (!homestayMediaListNew.contains(media)) {
                    homestayMediaRepository.delete(media);
                    storageService.deleteFile(media.getFilePath());
                }
            }
        }

        //Delete old Household Room Type Facility
        List<RoomTypeFacility> roomTypeFacilities = roomTypeFacilityRepository.findAllByHouseholdRoomTypeId(id);
        roomTypeFacilityRepository.deleteAll(roomTypeFacilities);

        //Save Household Room Type Facility to database
        List<FacilityDto> facilities = householdRoomTypeResponseDto.getFacilities();
        List<Facility> facilityList = facilityService.mapDtoListToFacilityList(facilities);
        List<RoomTypeFacility> roomTypeFacilityList = new ArrayList<>();

        if(facilityList != null){
            facilityList.forEach(facility -> {
                RoomTypeFacility roomTypeFacility = new RoomTypeFacility();
                roomTypeFacility.setFacility(facility);
                roomTypeFacility.setHouseholdRoomType(householdRoomTypeOptional.get());
                roomTypeFacilityList.add(roomTypeFacility);
                roomTypeFacilityRepository.save(roomTypeFacility);
            });
        }

        HouseholdRoomType householdRoomTypeNew = mapToEntity(householdRoomTypeResponseDto);
        if (oldPrice.compareTo(BigDecimal.valueOf(0)) == 0) {
            householdRoomTypeNew.setStatus(HouseholdTypeRoomStatus.ACTIVE_PRICE_SET_FIRST_TIME);
            createRequestPriceTypeRoom(householdId, id, newPrice, HouseholdTypeRoomStatus.ACTIVE_PRICE_SET_FIRST_TIME);
        } else {
            if (oldPrice.compareTo(newPrice) != 0) {
                householdRoomTypeNew.setStatus(HouseholdTypeRoomStatus.ACTIVE_PRICE_CHANGING);
                createRequestPriceTypeRoom(householdId, id, newPrice, HouseholdTypeRoomStatus.ACTIVE_PRICE_CHANGING);
            } else {
                householdRoomTypeNew.setStatus(HouseholdTypeRoomStatus.ACTIVE);
            }
        }

        householdRoomTypeNew.setId(id);
        householdRoomTypeNew.setHousehold(householdRoomType.getHousehold());
        householdRoomTypeNew.setRoomType(householdRoomType.getRoomType());
        householdRoomTypeNew.setRoomTypeFacilities(roomTypeFacilityList);
        householdRoomTypeNew.setHomestayMedias(homestayMediaListNew);
        householdRoomTypeRepository.save(householdRoomTypeNew);

        //Save images to storage and database
        List<MultipartFile> images = householdRoomTypeResponseDto.getImageFiles();
        if (images != null && !images.isEmpty()) {
            addImagesToRoomType(householdRoomTypeNew, images);
        }
        return true;
    }

    @Override
    public boolean deleteHouseholdRoomType(Long id) {
        Optional<HouseholdRoomType> householdRoomTypeOptional = householdRoomTypeRepository.findById(id);
        if (householdRoomTypeOptional.isEmpty()) {
            throw new ResourceNotFoundException("household.room.type.not.found");
        }
        HouseholdRoomType householdRoomType = householdRoomTypeOptional.get();
        householdRoomType.setStatus(HouseholdTypeRoomStatus.DELETED);
        householdRoomTypeRepository.save(householdRoomType);
        return true;
    }

    @Override
    public Optional<HouseholdRoomType> getByManagerEmailAndId(String s, Long id) {
        return householdRoomTypeRepository.getByManagerEmailAndId(s, id);
    }

    @Override
    public List<HouseholdRoomTypeDto> findHouseholdDormByHouseholdId(Long householdId) {
        List<HouseholdRoomTypeDto> householdRoomTypeDtoList = new ArrayList<>();
        List<Long> householdRoomTypeId = householdRoomTypeRepository.findIdDormByHouseholdId(householdId);
        for (Long id : householdRoomTypeId) {
            HouseholdRoomTypeDto householdRoomTypeDto = new HouseholdRoomTypeDto();
            Optional<HouseholdRoomType> householdRoomTypeOptional = householdRoomTypeRepository.findById(id);
            HouseholdRoomType householdRoomType = householdRoomTypeOptional.orElseThrow(() -> new ResourceNotFoundException("Household room type not found"));
            householdRoomTypeDto.setId(householdRoomType.getId());
            householdRoomTypeDto.setCapacity(householdRoomType.getCapacity());
            householdRoomTypeDto.setIsChildrenAndBed(householdRoomType.getIsChildrenAndBed());
            householdRoomTypeDto.setPrice(householdRoomType.getPrice());
            householdRoomTypeDto.setRoomTypeName(householdRoomType.getRoomType().getRoomTypeName());
            householdRoomTypeDto.setSingleBed(householdRoomType.getRoomType().getSingleBed());
            householdRoomTypeDto.setDoubleBed(householdRoomType.getRoomType().getDoubleBed());
            List<Facility> facilities = roomTypeFacilityRepository.findFacilitiesByHouseholdRoomTypeId(householdRoomType.getId());
            householdRoomTypeDto.setFacilities(facilityService.mapFacilityListToDtoList(facilities));
            householdRoomTypeDtoList.add(householdRoomTypeDto);
        }
        return householdRoomTypeDtoList;
    }
}

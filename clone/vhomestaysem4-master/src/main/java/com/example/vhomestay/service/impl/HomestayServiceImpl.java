package com.example.vhomestay.service.impl;

import com.example.vhomestay.enums.HomestayStatus;
import com.example.vhomestay.enums.MediaType;
import com.example.vhomestay.mapper.HomestayForSearchRoomManagerResponseMapper;
import com.example.vhomestay.enums.RoomStatus;
import com.example.vhomestay.model.dto.request.homestay.HomestayAdminRequestDto;
import com.example.vhomestay.model.dto.response.*;
import com.example.vhomestay.model.dto.response.area.AreaAdminResponseDto;
import com.example.vhomestay.model.dto.response.homestay.HomestayForSearchRoomManagerResponseDto;
import com.example.vhomestay.model.dto.response.homestay.admin.HomestayCommonAdminResponseDto;
import com.example.vhomestay.model.dto.response.homestay.admin.HomestayAdminResponseDto;
import com.example.vhomestay.model.dto.response.homestay.admin.HomestayListAdminResponseDto;
import com.example.vhomestay.model.dto.response.household.admin.HouseholdResponseDto;
import com.example.vhomestay.model.entity.*;
import com.example.vhomestay.repository.*;
import com.example.vhomestay.security.SecurityUtil;
import com.example.vhomestay.service.AreaService;
import com.example.vhomestay.service.HomestayService;
import com.example.vhomestay.service.StorageService;
import com.example.vhomestay.util.exception.ResourceInternalServerErrorException;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service("homestayService")
public class HomestayServiceImpl extends BaseServiceImpl<Homestay, Long, HomestayRepository>
        implements HomestayService {
    private final HomestayRepository homestayRepository;
    private final HouseholdRepository householdRepository;
    private final RoomRepository roomRepository;
    private final AreaRepository areaRepository;
    private final HomestayMediaRepository homestayMediaRepository;
    private final StorageService storageService;
    private final ModelMapper modelMapper;
    private final HomestayForSearchRoomManagerResponseMapper homestayForSearchRoomManagerResponseMapper;

    public HomestayServiceImpl(HomestayRepository homestayRepository, HouseholdRepository householdRepository, RoomRepository roomRepository, AreaRepository areaRepository, AreaService areaService, HomestayMediaRepository homestayMediaRepository, StorageService storageService, ModelMapper modelMapper, HomestayForSearchRoomManagerResponseMapper homestayForSearchRoomManagerResponseMapper) {
        this.homestayRepository = homestayRepository;
        this.householdRepository = householdRepository;
        this.roomRepository = roomRepository;
        this.areaRepository = areaRepository;
        this.homestayMediaRepository = homestayMediaRepository;
        this.storageService = storageService;
        this.modelMapper = modelMapper;
        this.homestayForSearchRoomManagerResponseMapper = homestayForSearchRoomManagerResponseMapper;
        modelMapperConfiguration();
    }

    private void modelMapperConfiguration() {
        modelMapper.createTypeMap(Homestay.class, HomestayInformationForManager.class)
                .addMapping(Homestay::getId, HomestayInformationForManager::setHomestayId)
                .addMapping(Homestay::getHomestayCode, HomestayInformationForManager::setHomestayCode)
                .addMapping(Homestay::getStatus, HomestayInformationForManager::setHomestayStatus)
                .addMapping(Homestay::getFullAddress, HomestayInformationForManager::setAddress)
                .addMapping(Homestay::getMedias, HomestayInformationForManager::setHomestayMediaList);
    }

    @Override
    public List<HomestayInformationForManager> getAllHomestayDetailForManager(String managerEmail) {
        List<HomestayInformationForManager> homestayInformationForManagerList = new ArrayList<>();
        List<Homestay> homestayList = homestayRepository.findAllHomestayByManagerEmail(managerEmail);
        Integer countAllRoom = 0;
        Integer countAllDorm = 0;
        for (int i = 0; i < homestayList.size(); i++) {
            countAllRoom = roomRepository.countAllRoomByHomestayId(homestayList.get(i).getId());
            countAllDorm = roomRepository.countAllDormByHomestayId(homestayList.get(i).getId());
            homestayInformationForManagerList.add(
                    new HomestayInformationForManager(homestayList.get(i).getId(),
                            homestayList.get(i).getHomestayCode(), countAllRoom, countAllDorm,
                            homestayList.get(i).getStatus(), homestayList.get(i).getFullAddress(),
                            homestayList.get(i).getMedias()));
        }
        return homestayInformationForManagerList;
    }

    @Override
    public boolean editHomestay(Long homestayId, HomestayInformationForManager homestayInformationForManager) throws IOException {
        Optional<Homestay> homestayOptional = homestayRepository.findById(homestayId);
        if (homestayOptional.isEmpty()) {
            throw new ResourceNotFoundException("homestay.not.found");
        }
        Homestay homestay = homestayOptional.get();
        homestay.setFullAddress(homestayInformationForManager.getAddress());
        homestay.setStatus(homestayInformationForManager.getHomestayStatus());

        //Delete old Media
        List<HomestayMedia> homestayMediaListOld = homestayMediaRepository.findAllByHomestayId(homestayId);
        List<HomestayMedia> homestayMediaListNew = homestayInformationForManager.getHomestayMediaList();
        if (homestayMediaListNew != null && !homestayMediaListNew.isEmpty()) {
            for (HomestayMedia media : homestayMediaListNew) {
                media.setHomestay(homestay);
            }
            for (HomestayMedia media : homestayMediaListOld) {
                if (!homestayMediaListNew.contains(media)) {
                    homestayMediaRepository.delete(media);
                    storageService.deleteFile(media.getFilePath());
                }
            }
        }
        homestay.setMedias(homestayMediaListNew);
        homestayRepository.save(homestay);

        //Save images to storage and database
        List<MultipartFile> images = homestayInformationForManager.getImagesFile();
        if (images != null && !images.isEmpty()) {
            addImagesToHomestay(homestay, images);
        }
        return true;
    }

    @Override
    public void addImagesToHomestay(Homestay homestay, List<MultipartFile> images) throws IOException {
        if (images == null || images.isEmpty()) {
            return;
        }
        for (MultipartFile image : images) {
            String imageName = image.getOriginalFilename();
            String imageUrl = storageService.uploadFile(image);
            saveImageToDatabase(homestay, imageName, imageUrl);
        }
    }

    @Override
    public boolean addHomestayInfo(Long id, HomestayCreateFormResponseDto request) throws IOException {
        Optional<Household> householdOptional = householdRepository.findById(id);
        if (householdOptional.isEmpty()) {
            throw new ResourceNotFoundException("household.not.found");
        }
        String homestayCode = request.getHomestayCode();
        Optional<Homestay> homestayOptional = homestayRepository.findByHomestayCode(homestayCode);
        Homestay homestay = homestayOptional.orElseGet(Homestay::new);
        homestay.setHousehold(householdOptional.get());
        homestay.setHomestayCode(homestayCode);
        homestay.setFullAddress(request.getAddress());
        homestay.setStatus(HomestayStatus.ACTIVE);
        homestayRepository.save(homestay);
        return true;
    }

    @Override
    public HomestayInformationForManager mapToDTOForDetail(Homestay homestay) {
        return modelMapper.map(homestay, HomestayInformationForManager.class);
    }

    @Override
    public Homestay mapToEntity(HomestayInformationForManager homestayInformationForManager) {
        return modelMapper.map(homestayInformationForManager, Homestay.class);
    }

    @Override
    public boolean deletedHomestay(Long homestayId) {
        Optional<Homestay> homestayOptional = homestayRepository.findById(homestayId);
        List<Room> roomList = roomRepository.findAllByHomestayId(homestayId);
        if (homestayOptional.isEmpty()) {
            throw new ResourceNotFoundException("homestay.not.found");
        }
        for (Room room : roomList) {
            room.setStatus(RoomStatus.DELETED);
            roomRepository.save(room);
        }
        Homestay homestay = homestayOptional.get();
        homestay.setStatus(HomestayStatus.DELETED);
        homestayRepository.save(homestay);
        return true;
    }

    @Override
    public HomestayInformationForManager getHomestayDetail(Long homestayId) {
        Optional<Homestay> homestayOptional = homestayRepository.findById(homestayId);
        if (homestayOptional.isEmpty()) {
            throw new ResourceNotFoundException("homestay.not.found");
        }
        Homestay homestay = homestayOptional.get();
        HomestayInformationForManager homestayInformationForManager = mapToDTOForDetail(homestay);
        Integer totalRoom = roomRepository.countAllRoomByHomestayId(homestayId);
        Integer totalDorm = roomRepository.countAllDormByHomestayId(homestayId);
        homestayInformationForManager.setTotalRoom(totalRoom);
        homestayInformationForManager.setTotalDorm(totalDorm);
        List<HomestayMedia> homestayMediaList = homestayMediaRepository.findAllByHomestayId(homestayId);
        homestayInformationForManager.setHomestayMediaList(homestayMediaList);
        return homestayInformationForManager;
    }

    @Override
    public boolean hideOrShowHomestay(Long homestayId) {
        Optional<Homestay> homestayOptional = homestayRepository.findById(homestayId);
        List<Room> roomList = roomRepository.findAllByHomestayId(homestayId);
        if (homestayOptional.isEmpty()) {
            throw new ResourceNotFoundException("homestay.not.found");
        }
        Homestay homestay = homestayOptional.get();
        if (homestay.getStatus().equals(HomestayStatus.INACTIVE)) {
            homestay.setStatus(HomestayStatus.ACTIVE);
            for (Room room : roomList) {
                room.setStatus(RoomStatus.ACTIVE);
                roomRepository.save(room);
            }
        } else {
            homestay.setStatus(HomestayStatus.INACTIVE);
            for (Room room : roomList) {
                room.setStatus(RoomStatus.INACTIVE);
                roomRepository.save(room);
            }
        }
        homestayRepository.save(homestay);
        return true;
    }

    @Override
    public Optional<Homestay> findHomestayById(Long homestayId) {
        return homestayRepository.findHomestayById(homestayId);
    }

    @Override
    public List<HomestayForSearchRoomManagerResponseDto> showHomestaysForSearchRooms() {
        String emailManager = SecurityUtil.getCurrentUserLogin().get();

        List<Homestay> homestays = homestayRepository.findAllHomestayByManagerEmail(emailManager);

        List<HomestayForSearchRoomManagerResponseDto> homestayForSearchRoomManagerResponseDtos = new ArrayList<>();
        HomestayForSearchRoomManagerResponseDto homestayForSearchRoomManagerResponseDto;
        for (Homestay h : homestays) {
            homestayForSearchRoomManagerResponseDto = homestayForSearchRoomManagerResponseMapper.mapper(h);

            homestayForSearchRoomManagerResponseDtos.add(homestayForSearchRoomManagerResponseDto);
        }
        return homestayForSearchRoomManagerResponseDtos;
    }

    @Override
    public List<Homestay> getHomestaysByAreaId(Long id) {
        return homestayRepository.findHomestaysByAreaId(id);
    }

    @Override
    public List<HomestayListAdminResponseDto> findAllHomestaysByAdmin() {
        List<Area> areas = areaRepository.getAreasByAdmin();

        if (areas != null && areas.size() > 0) {
            // Khai báo 2 list để chứa kết quả trả về
            List<HomestayListAdminResponseDto> homestayListAdminResponseDtos = new ArrayList<>();
            HomestayListAdminResponseDto homestayListAdminResponseDto;

            List<HomestayCommonAdminResponseDto> homestayCommonAdminResponseDtos;
            HomestayCommonAdminResponseDto homestayCommonAdminResponseDto;

            List<HomestayMedia> homestayMediaList = new ArrayList<>();
            List<HomestayMediaDto> homestayMediaDtoList = new ArrayList<>();
            HomestayMediaDto homestayMediaDto;

            // Khai báo 1 list để chứa homestay theo area
            List<Homestay> homestays;

            for (Area area : areas) {
                homestayListAdminResponseDto = new HomestayListAdminResponseDto();
                homestayCommonAdminResponseDtos = new ArrayList<>();
                homestayListAdminResponseDto.setArea(area.getName());

                homestays = homestayRepository.findHomestaysByAreaId(area.getId());

                for (Homestay homestay : homestays) {
                    homestayCommonAdminResponseDto = new HomestayCommonAdminResponseDto();

                    homestayCommonAdminResponseDto.setId(homestay.getId());
                    homestayCommonAdminResponseDto.setHomestayCode(homestay.getHomestayCode());
                    homestayCommonAdminResponseDto.setCapacityOfHomestay(homestayRepository.capacityOfHomestay(homestay.getId()));
                    homestayCommonAdminResponseDto.setTotalRoomOfHomestay(roomRepository.countAllRoomByHomestayId(homestay.getId()));
                    homestayCommonAdminResponseDto.setTotalDormOfHomestay(homestayRepository.countAllDormSlotOfHomestayById(homestay.getId()));
                    homestayCommonAdminResponseDto.setFullAddress(homestay.getFullAddress());

                    homestayMediaList = homestay.getMedias();

                    if(homestayMediaList == null){
                        homestayMediaList = new ArrayList<>();
                    }

                    for (HomestayMedia homestayMedia : homestayMediaList) {
                        homestayMediaDto = new HomestayMediaDto();

                        homestayMediaDto.setId(homestayMedia.getId());
                        homestayMediaDto.setFilePath(homestayMedia.getFilePath());

                        homestayMediaDtoList.add(homestayMediaDto);
                    }

                    homestayCommonAdminResponseDto.setHomestayMediaDtoList(homestayMediaDtoList);
                    homestayCommonAdminResponseDto.setHomestayStatus(homestay.getStatus());
                    homestayCommonAdminResponseDto.setHouseholdName(homestay.getHousehold().getHouseholdName());

                    if(homestay.getHousehold().getManager() != null) {
                        String fullName = homestay.getHousehold().getManager().getFirstName() + " " + homestay.getHousehold().getManager().getLastName();
                        homestayCommonAdminResponseDto.setHouseholderName(fullName);
                        homestayCommonAdminResponseDto.setHouseholdPhone(homestay.getHousehold().getManager().getPhoneNumber());
                        homestayCommonAdminResponseDto.setHouseholdEmail(homestay.getHousehold().getManager().getAccount().getEmail());
                    }

                    homestayCommonAdminResponseDtos.add(homestayCommonAdminResponseDto);
                }

                homestayListAdminResponseDto.setHomestayList(homestayCommonAdminResponseDtos);
                homestayListAdminResponseDtos.add(homestayListAdminResponseDto);
            }
            return homestayListAdminResponseDtos;
        } else {
            throw new ResourceNotFoundException("area.is.empty");
        }
    }

    @Override
    public HomestayAdminResponseDto showHomestayFormByAdmin() {
        List<Area> areas = areaRepository.getAreasByAdmin();
        List<AreaAdminResponseDto> areaResponseDtos = new ArrayList<>();

        List<Household> households = householdRepository.findAllActive();
        List<HouseholdResponseDto> householdResponseDtos = new ArrayList<>();

        if (areas.size() > 0) {
            AreaAdminResponseDto areaResponseDto;
            for (Area area : areas) {
                areaResponseDto = new AreaAdminResponseDto();
                areaResponseDto.setId(area.getId());
                areaResponseDto.setName(area.getName());

                areaResponseDtos.add(areaResponseDto);
            }
        } else {
            throw new ResourceNotFoundException("area.is.empty");
        }

        if (households.size() > 0) {
            HouseholdResponseDto householdResponseDto;
            for (Household household : households) {
                householdResponseDto = new HouseholdResponseDto();
                householdResponseDto.setId(household.getId());
                householdResponseDto.setHouseholdName(household.getHouseholdName());

                householdResponseDtos.add(householdResponseDto);
            }
        } else {
            throw new ResourceNotFoundException("household.is.empty");
        }

        HomestayAdminResponseDto homestayAdminResponseDto = new HomestayAdminResponseDto();
        homestayAdminResponseDto.setAreaAdminResponseDtoList(areaResponseDtos);
        homestayAdminResponseDto.setHouseholdResponseDtoList(householdResponseDtos);

        return homestayAdminResponseDto;
    }

    @Override
    public void addHomestayByAdmin(HomestayAdminRequestDto homestayAdminRequestDto) {
        Homestay homestay = new Homestay();
        homestay.setHomestayCode(homestayAdminRequestDto.getHomestayName());

        Area area = areaRepository.getAreaByAdmin(homestayAdminRequestDto.getAreaId()).orElseThrow(() -> new ResourceNotFoundException("Area not found"));
        homestay.setArea(area);

        Household household = householdRepository.findByIdAndStatusActive(homestayAdminRequestDto.getHouseholdId()).orElseThrow(() -> new ResourceNotFoundException("Household not found"));
        homestay.setHousehold(household);

        homestay.setStatus(HomestayStatus.ACTIVE);
        try {
            homestayRepository.save(homestay);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("homestay.save.failed");
        }
    }

    @Override
    public void updateHomestayByAdmin(HomestayAdminRequestDto homestayAdminRequestDto) {
        Homestay homestay = homestayRepository.findHomestayById(homestayAdminRequestDto.getHomestayId()).orElseThrow(() -> new ResourceNotFoundException("Homestay not found"));

        // Set giá trị mới
        homestay.setHomestayCode(homestayAdminRequestDto.getHomestayName());
        Area area = areaRepository.getAreaByAdmin(homestayAdminRequestDto.getAreaId()).orElseThrow(() -> new ResourceNotFoundException("Area not found"));
        homestay.setArea(area);

        Household household = householdRepository.findByIdAndStatusActive(homestayAdminRequestDto.getHouseholdId()).orElseThrow(() -> new ResourceNotFoundException("Household not found"));
        homestay.setHousehold(household);

        try {
            homestayRepository.save(homestay);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("homestay.save.failed");
        }
    }

    @Override
    public Optional<HomestayCommonAdminResponseDto> findHomestayCommonInformationByAdmin(Long homestayId) {
        Homestay homestay = homestayRepository.findHomestayById(homestayId).orElseThrow(() -> new ResourceNotFoundException("Homestay not found"));

        HomestayCommonAdminResponseDto homestayCommonAdminResponseDto = new HomestayCommonAdminResponseDto();
        homestayCommonAdminResponseDto.setId(homestay.getId());
        homestayCommonAdminResponseDto.setHomestayCode(homestay.getHomestayCode());
        homestayCommonAdminResponseDto.setCapacityOfHomestay(homestayRepository.capacityOfHomestay(homestay.getId()));
        homestayCommonAdminResponseDto.setTotalRoomOfHomestay(roomRepository.countAllRoomByHomestayId(homestay.getId()));
        homestayCommonAdminResponseDto.setTotalDormOfHomestay(homestayRepository.countAllDormSlotOfHomestayById(homestay.getId()));
        homestayCommonAdminResponseDto.setFullAddress(homestay.getFullAddress());
        List<HomestayMedia> homestayMediaList = homestay.getMedias();
        List<HomestayMediaDto> homestayMediaDtoList = new ArrayList<>();
        HomestayMediaDto homestayMediaDto;

        for (HomestayMedia homestayMedia : homestayMediaList) {
            homestayMediaDto = new HomestayMediaDto();
            homestayMediaDto.setId(homestayMedia.getId());
            homestayMediaDto.setFilePath(homestayMedia.getFilePath());
            homestayMediaDtoList.add(homestayMediaDto);
        }

        homestayCommonAdminResponseDto.setHomestayMediaDtoList(homestayMediaDtoList);
        homestayCommonAdminResponseDto.setHomestayStatus(homestay.getStatus());
        homestayCommonAdminResponseDto.setHouseholdName(homestay.getHousehold().getHouseholdName());
        homestayCommonAdminResponseDto.setHouseholdName(homestay.getHousehold().getHouseholdName());
        String fullName = homestay.getHousehold().getManager().getFirstName() + " " + homestay.getHousehold().getManager().getLastName();
        homestayCommonAdminResponseDto.setHouseholderName(fullName);
        homestayCommonAdminResponseDto.setHouseholdPhone(homestay.getHousehold().getPhoneNumberFirst());
        homestayCommonAdminResponseDto.setHouseholdEmail(homestay.getHousehold().getManager().getAccount().getEmail());

        return Optional.of(homestayCommonAdminResponseDto);
    }

    @Override
    public Optional<Homestay> getHomestayByManagerEmailAndHomestayId(String managerEmail, Long homestayId) {
        return homestayRepository.getHomestayByManagerEmailAndHomestayId(managerEmail, homestayId);
    }

    @Override
    public void deleteHomestayByAdmin(Long homestayId) {
        Homestay homestay = homestayRepository.findHomestayById(homestayId).orElseThrow(() -> new ResourceNotFoundException("Homestay not found"));
        homestay.setStatus(HomestayStatus.DELETED);
        try {
            homestayRepository.save(homestay);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("homestay.delete.failed");
        }
    }

    @Override
    public List<HomestayDto> findHomestayByHouseholdId(Long householdId) {
        return homestayRepository.findAllByHouseholdId(householdId);
    }

    private void saveImageToDatabase(Homestay homestay, String imageName, String imageUrl) {
        HomestayMedia homestayMedia = new HomestayMedia();
        homestayMedia.setHomestay(homestay);
        homestayMedia.setFileName(imageName);
        homestayMedia.setFilePath(imageUrl);
        homestayMedia.setType(MediaType.IMAGE);
        homestayMediaRepository.save(homestayMedia);
    }


}

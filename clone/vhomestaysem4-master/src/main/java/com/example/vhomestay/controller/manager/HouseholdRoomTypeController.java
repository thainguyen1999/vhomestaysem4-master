package com.example.vhomestay.controller.manager;

import com.example.vhomestay.model.dto.response.*;
import com.example.vhomestay.model.entity.*;
import com.example.vhomestay.security.SecurityUtil;
import com.example.vhomestay.service.*;
import com.example.vhomestay.service.HouseholdService;
import com.example.vhomestay.service.impl.HouseholdServiceImpl;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/manager/room-type")
@RequiredArgsConstructor
public class HouseholdRoomTypeController {
    private final RoomTypeService roomTypeService;
    private final FacilityService facilityService;
    private final HouseholdRoomTypeService householdRoomTypeService;
    private final HouseholdService householdService;

    @GetMapping
    public HouseholdRoomTypeListResponseDto getAllHouseholdRoomType() {
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        String currentUserLoginEmail = currentUserLoginEmailOptional.get();
        List<HouseholdRoomTypeResponseDto> householdRoomTypeResponseDtoList
                = householdRoomTypeService.getAllHouseholdRoomTypeThatManageIsManaged(currentUserLoginEmail);
        return HouseholdRoomTypeListResponseDto.builder()
                .householdRoomTypeListForManager(householdRoomTypeResponseDtoList)
                .build();
    }

    @GetMapping("/form-data")
    public HouseholdRoomTypeCreateFormResponseDto addRoomType() {
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        if (currentUserLoginEmailOptional.isEmpty()){
            throw new ResourceBadRequestException("account.not.found");
        }
        Household household = householdService.getHouseholdByManagerEmail(currentUserLoginEmailOptional.get()).get();
        Long householdId = household.getId();
        List<RoomType> roomTypeList = roomTypeService.findRoomTypeNotInHouseholdRoomType(householdId);
        if (roomTypeList.isEmpty()){
        throw new ResourceBadRequestException("room.type.do.not.have.room.type.or.no.permission");
        }
        List<Facility> facilityList = facilityService.findAll();
        if (facilityList.isEmpty()){
            throw new ResourceBadRequestException("no.facility.exits");
        }
        List<RoomTypeDto> roomTypeDtoList = roomTypeService.mapRoomTypeListToDtoList(roomTypeList);
        List<FacilityDto> facilityDtoList = facilityService.mapFacilityListToDtoList(facilityList);

        return HouseholdRoomTypeCreateFormResponseDto.builder()
                .roomTypes(roomTypeDtoList)
                .facilities(facilityDtoList)
                .isChildrenAndBed(false)
                .build();
    }


    @PostMapping()
    public ResponseEntity<?> addHouseholdRoomType(@ModelAttribute HouseholdRoomTypeCreateFormResponseDto householdRoomTypeCreateFormResponseDto)
            throws IOException {
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        if (currentUserLoginEmailOptional.isEmpty()){
            throw new ResourceBadRequestException("account.not.found");
        }
        Household household = householdService.getHouseholdByManagerEmail(currentUserLoginEmailOptional.get()).get();
        if(householdRoomTypeService.addHouseholdRoomType(householdRoomTypeCreateFormResponseDto, household.getId())){
            MessageResponseDto messageResponseDto = new MessageResponseDto("room.type.add.success", HttpStatus.OK);
            return ResponseEntity.ok(messageResponseDto);
        } else {
            throw new ResourceBadRequestException("room.type.add.failed");
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public HouseholdRoomTypeResponseDto showHouseholdRoomTypeDetail(@PathVariable("id") Long id) {
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        if (currentUserLoginEmailOptional.isEmpty()){
            throw new ResourceBadRequestException("account.not.found");
        }
        Optional<HouseholdRoomType> householdRoomTypeOptional = householdRoomTypeService.getByManagerEmailAndId(currentUserLoginEmailOptional.get(), id);
        if (householdRoomTypeOptional.isEmpty()){
            throw new ResourceBadRequestException("manager.room.type.not.found");
        }
        HouseholdRoomType householdRoomType = householdRoomTypeService.findById(id).get();
        HouseholdRoomTypeResponseDto householdRoomTypeResponseDto = householdRoomTypeService.mapToDTO(householdRoomType);
        householdRoomTypeResponseDto.setHouseholdRoomTypeId(id);
        return householdRoomTypeResponseDto;
    }

    @GetMapping("/{id}/form-data")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public HouseholdRoomTypeResponseDto editHouseholdRoomType(@PathVariable("id") Long id) {
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        if (currentUserLoginEmailOptional.isEmpty()){
            throw new ResourceBadRequestException("account.not.found");
        }
        HouseholdRoomType householdRoomType = householdRoomTypeService.findById(id).get();
        List<FacilityDto> facilityVillageList = facilityService.mapFacilityListToDtoList(facilityService.findAll());
        HouseholdRoomTypeResponseDto householdRoomTypeResponseDto = householdRoomTypeService.mapToDTO(householdRoomType);
        householdRoomTypeResponseDto.setFacilityVillageList(facilityVillageList);
        return householdRoomTypeResponseDto;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> editHouseholdRoomType(@PathVariable("id") Long id,
                                           @ModelAttribute HouseholdRoomTypeResponseDto householdRoomTypeResponseDto) throws IOException {
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        if (currentUserLoginEmailOptional.isEmpty()){
            throw new ResourceBadRequestException("account.not.found");
        }
        if (householdRoomTypeService.editHouseholdRoomType(id, householdRoomTypeResponseDto)){
            MessageResponseDto messageResponseDto = new MessageResponseDto("household.room.type.update.success", HttpStatus.OK);
            return ResponseEntity.ok(messageResponseDto);
        } else {
            throw new ResourceBadRequestException("household.room.type.update.failed");
        }
    }

//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_MANAGER')")
//    public ResponseEntity<?> deleteHouseholdRoomType(@PathVariable("id") Long id) {
//        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
//        if (currentUserLoginEmailOptional.isEmpty()) {
//            throw new ResourceBadRequestException("User not found");
//        }
//        if (householdRoomTypeService.deleteHouseholdRoomType(id)) {
//            MessageResponseDto messageResponseDto = new MessageResponseDto("Delete room type successfully", HttpStatus.OK);
//            return ResponseEntity.ok(messageResponseDto);
//        } else {
//            throw new ResourceBadRequestException("Delete room type failed");
//        }
//    }

}

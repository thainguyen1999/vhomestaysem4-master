package com.example.vhomestay.service;

import com.example.vhomestay.enums.HouseholdTypeRoomStatus;
import com.example.vhomestay.model.dto.response.HouseholdRoomTypeCreateFormResponseDto;
import com.example.vhomestay.model.dto.response.HouseholdRoomTypeDto;
import com.example.vhomestay.model.dto.response.HouseholdRoomTypeResponseDto;
import com.example.vhomestay.model.entity.Household;
import com.example.vhomestay.model.entity.HouseholdRoomType;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface HouseholdRoomTypeService {
    @Transactional
    boolean addHouseholdRoomType(HouseholdRoomTypeCreateFormResponseDto householdRoomTypeCreateFormResponseDto, Long id) throws IOException;
    Optional<HouseholdRoomType> findByHouseholdIdAndRoomTypeId(Long householdId, Long roomTypeId);
    List<HouseholdRoomTypeDto> findHouseholdRoomTypeByHouseholdId(Long householdId);
    List<HouseholdRoomTypeResponseDto> getAllHouseholdRoomTypeThatManageIsManaged(String managerEmail);
    Optional<HouseholdRoomType> findById(Long id);
    HouseholdRoomTypeResponseDto mapToDTO(HouseholdRoomType householdRoomType);
    HouseholdRoomType mapToEntity(HouseholdRoomTypeResponseDto householdRoomTypeResponseDto);
    HouseholdRoomTypeResponseDto showHouseholdRoomTypeForEdit(Long householdRoomTypeId, Household household);
    boolean createRequestPriceTypeRoom(Long householdId, Long householdRoomTypeId, BigDecimal price, HouseholdTypeRoomStatus status);
    @Transactional
    boolean editHouseholdRoomType(Long id, HouseholdRoomTypeResponseDto householdRoomTypeResponseDto) throws IOException;
    boolean deleteHouseholdRoomType(Long id);
    Optional<HouseholdRoomType> getByManagerEmailAndId(String s, Long id);
    List<HouseholdRoomTypeDto> findHouseholdDormByHouseholdId(Long householdId);
}

package com.example.vhomestay.service;

import com.example.vhomestay.model.dto.response.RoomTypeDto;
import com.example.vhomestay.model.entity.RoomType;

import java.util.List;
import java.util.Optional;

public interface RoomTypeService {
    List<RoomType> findAll();

    RoomTypeDto mapRoomTypeToDto(RoomType roomType);

    RoomType mapDtoToRoomType(RoomTypeDto roomTypeDto);

    List<RoomTypeDto> mapRoomTypeListToDtoList(List<RoomType> roomTypeList);

    List<RoomType> findRoomTypeNotInHouseholdRoomType(Long householdId);
    void addRoomTypeByAdmin(RoomTypeDto roomTypeDto);
    List<RoomTypeDto> getAllRoomTypesByAdmin();
    void updateRoomTypeByAdmin(RoomTypeDto roomTypeDto);
    Optional<RoomTypeDto> getRoomTypeById(Long id);
}

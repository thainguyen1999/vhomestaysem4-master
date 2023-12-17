package com.example.vhomestay.service.impl;


import com.example.vhomestay.model.dto.response.RoomTypeDto;
import com.example.vhomestay.model.entity.RoomType;
import com.example.vhomestay.repository.RoomTypeRepository;
import com.example.vhomestay.service.AdminService;
import com.example.vhomestay.service.RoomTypeService;
import com.example.vhomestay.util.exception.ResourceInternalServerErrorException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomTypeServiceImpl extends BaseServiceImpl<RoomType, Long, RoomTypeRepository>
        implements RoomTypeService {
    private final RoomTypeRepository roomTypeRepository;
    private final ModelMapper modelMapper;

    public RoomTypeServiceImpl(RoomTypeRepository roomTypeRepository, ModelMapper modelMapper) {
        this.roomTypeRepository = roomTypeRepository;
        this.modelMapper = modelMapper;
        modelMapperConfiguration();
    }

    private void modelMapperConfiguration() {
        modelMapper.createTypeMap(RoomType.class, RoomTypeDto.class)
                .addMapping(RoomType::getId, RoomTypeDto::setId)
                .addMapping(RoomType::getRoomTypeName, RoomTypeDto::setRoomTypeName)
                .addMapping(RoomType::getIsDorm, RoomTypeDto::setIsDorm)
                .addMapping(RoomType::getSingleBed, RoomTypeDto::setSingleBed)
                .addMapping(RoomType::getDoubleBed, RoomTypeDto::setDoubleBed);
    }

    @Override
    public List<RoomType> findAll() {
        return roomTypeRepository.findAll();
    }

    @Override
    public RoomTypeDto mapRoomTypeToDto(RoomType roomType) {
        return modelMapper.map(roomType, RoomTypeDto.class);
    }

    @Override
    public RoomType mapDtoToRoomType(RoomTypeDto roomTypeDto) {
        return modelMapper.map(roomTypeDto, RoomType.class);
    }

    @Override
    public List<RoomTypeDto> mapRoomTypeListToDtoList(List<RoomType> roomTypeList) {
        return roomTypeList.stream()
                .map(this::mapRoomTypeToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomType> findRoomTypeNotInHouseholdRoomType(Long householdId) {
        return roomTypeRepository.findRoomTypeNotInHouseholdRoomType(householdId);
    }

    @Override
    public void addRoomTypeByAdmin(RoomTypeDto roomTypeDto) {
        RoomType roomType = mapDtoToRoomType(roomTypeDto);
        try {
            roomTypeRepository.save(roomType);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("room.type.add.failed");
        }
    }

    @Override
    public List<RoomTypeDto> getAllRoomTypesByAdmin() {
        List<RoomType> roomTypeList = roomTypeRepository.findAll();

        List<RoomTypeDto> roomTypeDtos = new ArrayList<>();
        RoomTypeDto roomTypeDto;

        if (roomTypeList.size() > 0) {
            for (RoomType roomType : roomTypeList) {
                roomTypeDto = mapRoomTypeToDto(roomType);
                roomTypeDtos.add(roomTypeDto);
            }
        }

        return roomTypeDtos;
    }

    @Override
    public void updateRoomTypeByAdmin(RoomTypeDto roomTypeDto) {
        RoomType r = roomTypeRepository.findById(roomTypeDto.getId()).orElseThrow(() -> new ResourceInternalServerErrorException("Room type not found"));

        r.setRoomTypeName(roomTypeDto.getRoomTypeName());
        r.setIsDorm(roomTypeDto.getIsDorm());
        r.setSingleBed(roomTypeDto.getSingleBed());
        r.setDoubleBed(roomTypeDto.getDoubleBed());
        try {
            roomTypeRepository.save(r);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("room.type.update.failed");
        }
    }

    @Override
    public Optional<RoomTypeDto> getRoomTypeById(Long id) {
        Optional<RoomType> roomType = roomTypeRepository.findById(id);
        return roomType.map(this::mapRoomTypeToDto);
    }

}

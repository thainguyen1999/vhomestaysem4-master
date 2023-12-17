package com.example.vhomestay.mapper;

import com.example.vhomestay.model.dto.response.booking.customer.RoomTypeHouseholdAvailableDto;
import com.example.vhomestay.model.dto.response.booking.customer.RoomTypeHouseholdAvailableWithFullInfoDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoomTypeHouseholdForBookingResponseMapper {
    private final ModelMapper modelMapper;

    public RoomTypeHouseholdForBookingResponseMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        configureModelMapper();
    }

    private void configureModelMapper() {
        modelMapper.addMappings(new PropertyMap<RoomTypeHouseholdAvailableDto, RoomTypeHouseholdAvailableWithFullInfoDto>() {
            @Override
            protected void configure() {
                map().setRoomTypeHouseholdId(source.getRoomTypeHouseholdId());
                map().setRoomTypeName(source.getRoomTypeName());
                map().setCapacity(source.getCapacity());
                map().setSingleBed(source.getSingleBed());
                map().setDoubleBed(source.getDoubleBed());
                map().setPrice(source.getPrice());
                map().setIsChildrenBed(source.getIsChildrenBed());
                map().setIsDorm(source.getIsDorm());
                map().setQuantity(source.getQuantity());
            }
        });
    }

    public RoomTypeHouseholdAvailableWithFullInfoDto mapper(RoomTypeHouseholdAvailableDto roomTypeHouseholdAvailableDto) {
        return modelMapper.map(roomTypeHouseholdAvailableDto, RoomTypeHouseholdAvailableWithFullInfoDto.class);
    }
    public List<RoomTypeHouseholdAvailableWithFullInfoDto> mapper(List<RoomTypeHouseholdAvailableDto> roomTypeHouseholdAvailableDtoList) {
        if (roomTypeHouseholdAvailableDtoList == null) {
            return null;
        }
        return roomTypeHouseholdAvailableDtoList.stream()
                .map(this::mapper)
                .collect(Collectors.toList());
    }
}

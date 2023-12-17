package com.example.vhomestay.mapper;

import com.example.vhomestay.model.dto.response.booking.customer.BookingDetailRecommendDto;
import com.example.vhomestay.model.dto.response.booking.customer.RoomTypeHouseholdAvailableDto;
import com.example.vhomestay.model.dto.response.booking.customer.RoomTypeHouseholdAvailableWithFullInfoDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class BookingDetailRecommendResponseMapper {
    private final ModelMapper modelMapper;

    public BookingDetailRecommendResponseMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        configureModelMapper();
    }

    private void configureModelMapper() {
        modelMapper.addMappings(new PropertyMap<RoomTypeHouseholdAvailableWithFullInfoDto, BookingDetailRecommendDto>() {
            @Override
            protected void configure() {
                map().setRoomTypeHouseholdId(source.getRoomTypeHouseholdId());
                map().setRoomTypeName(source.getRoomTypeName());
                map().setPrice(source.getPrice());
                map().setCapacity(source.getCapacity());
                map().setSingleBed(source.getSingleBed());
                map().setDoubleBed(source.getDoubleBed());
            }
        });
    }

    public BookingDetailRecommendDto mapper(RoomTypeHouseholdAvailableWithFullInfoDto roomTypeHouseholdAvailableDto) {
        if (roomTypeHouseholdAvailableDto == null) {
            return null;
        }
        return modelMapper.map(roomTypeHouseholdAvailableDto, BookingDetailRecommendDto.class);
    }
}

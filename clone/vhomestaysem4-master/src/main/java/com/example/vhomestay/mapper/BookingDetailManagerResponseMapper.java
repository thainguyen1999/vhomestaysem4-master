package com.example.vhomestay.mapper;

import com.example.vhomestay.model.dto.response.booking.manager.BookingDetailManagerResponseDto;
import com.example.vhomestay.model.entity.BookingDetail;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class BookingDetailManagerResponseMapper {
    private final ModelMapper modelMapper;

    public BookingDetailManagerResponseMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        configureModelMapper();
    }

    private void configureModelMapper() {
        modelMapper.addMappings(new PropertyMap<BookingDetail, BookingDetailManagerResponseDto>() {
            @Override
            protected void configure() {
                map().setId(source.getId());
                map().setBookingCode(source.getBooking().getBookingCode());
                map().setHomestayCode(source.getRoom().getHomestay().getHomestayCode());
                map().setRoomTypeName(source.getRoom().getHouseholdRoomType().getRoomType().getRoomTypeName());
                map().setRoomName(source.getRoom().getRoomName());
                map().setSlotNumber(source.getDormSlot().getSlotNumber());
                map().setCheckInCustomerName(source.getBooking().getCheckInName());
                map().setPrice(source.getPrice());
                map().setBookingDetailStatus(source.getStatus());
                map().setBookingStatus(source.getBooking().getStatus());
                map().setIsDorm(source.getRoom().getHouseholdRoomType().getRoomType().getIsDorm());
            }
        });
    }

    public BookingDetailManagerResponseDto mapper(BookingDetail bookingDetail) {
        return modelMapper.map(bookingDetail, BookingDetailManagerResponseDto.class);
    }
}

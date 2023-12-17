package com.example.vhomestay.mapper;

import com.example.vhomestay.model.dto.response.BookingResponseDto;
import com.example.vhomestay.model.entity.Booking;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class BookingCustomerResponseMapper {
    private final ModelMapper modelMapper;

    public BookingCustomerResponseMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        configureModelMapper();
    }

    private void configureModelMapper() {
        modelMapper.addMappings(new PropertyMap<Booking, BookingResponseDto>() {
            @Override
            protected void configure() {
                map().setHouseholdId(source.getHousehold().getId());
                map().setHouseholdImage(source.getHousehold().getAvatar());
                map().setBookingCode(source.getBookingCode());
                map().setCheckInDate(source.getCheckInDate());
                map().setCheckOutDate(source.getCheckOutDate());
                map().setHouseholdName(source.getHousehold().getHouseholdName());
                map().setHouseholdCheckInTime(source.getHousehold().getCheckInTime());
                map().setHouseholdCheckOutTime(source.getHousehold().getCheckOutTime());
                map().setHouseholdPhoneNumberFirst(source.getHousehold().getPhoneNumberFirst());
                map().setHouseholdPhoneNumberSecond(source.getHousehold().getPhoneNumberSecond());
                map().setBookingCheckInName(source.getCheckInName());
                map().setBookingCheckInPhoneNumber(source.getCheckInPhoneNumber());
                map().setTotalNight(source.getTotalNight());
                map().setCancellationPeriod(source.getHousehold().getCancellationPeriod());
                map().setTotalRoom(source.getTotalRoom());
                map().setTotalGuest(source.getTotalGuest());
                map().setTotalPrice(source.getTotalPrice());
                map().setStatus(source.getStatus());
                map().setCancellationHistory(source.getCancellationHistory());
            }
        });

    }

    public BookingResponseDto mapToCustomerDTO(Booking booking) {
        return modelMapper.map(booking, BookingResponseDto.class);
    }
    public Booking mapToCustomerEntity(BookingResponseDto bookingResponseDto) {
        return modelMapper.map(bookingResponseDto, Booking.class);
    }
}

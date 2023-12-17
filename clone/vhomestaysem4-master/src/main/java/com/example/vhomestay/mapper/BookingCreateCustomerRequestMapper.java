package com.example.vhomestay.mapper;

import com.example.vhomestay.model.dto.request.booking.BookingCreateCustomerRequestDto;
import com.example.vhomestay.model.entity.Booking;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class BookingCreateCustomerRequestMapper {
    private final ModelMapper modelMapper;
    public BookingCreateCustomerRequestMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        configureModelMapper();
    }

    private void configureModelMapper() {
        modelMapper.addMappings(new PropertyMap<BookingCreateCustomerRequestDto, Booking>() {
            @Override
            protected void configure() {
                map().setCheckInDate(source.getCheckInDate());
                map().setCheckOutDate(source.getCheckOutDate());
                map().setTotalNight(source.getTotalNight());
                map().setTotalGuest(source.getNumberOfGuests());
                map().setCheckInName(source.getCustomerName());
                map().setCheckInPhoneNumber(source.getCustomerPhone());
                map().setTotalPrice(source.getTotalPrice());
            }
        });
    }

    public Booking mapToBooking(BookingCreateCustomerRequestDto bookingCreateCustomerRequestDto) {
        return modelMapper.map(bookingCreateCustomerRequestDto, Booking.class);
    }
}


package com.example.vhomestay.mapper;

import com.example.vhomestay.model.dto.response.booking.manager.BookingManagerResponseDto;
import com.example.vhomestay.model.entity.Booking;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class BookingManagerResponseMapper {
    private final ModelMapper modelMapper;

    public BookingManagerResponseMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        configureModelMapper();
    }

    private void configureModelMapper() {
        modelMapper.addMappings(new PropertyMap<Booking, BookingManagerResponseDto>() {
            @Override
            protected void configure() {
                map().setBookingCode(source.getBookingCode());
                map().setCreatedDate(source.getCreatedDate());
                map().setBookingCustomerName(source.getCheckInName());
                map().setBookingCustomerPhoneNumber(source.getCheckInPhoneNumber());
                map().setTotalNight(source.getTotalNight());
                map().setCheckInDate(source.getCheckInDate());
                map().setCheckOutDate(source.getCheckOutDate());
                map().setTotalGuest(source.getTotalGuest());
                map().setTotalRoom(source.getTotalRoom());
                map().setTotalPrice(source.getTotalPrice());
                map().setBookingStatus(source.getStatus());
                map().setCustomerBankInformation(source.getCustomer().getCustomerBankInformation());
                map().setPaymentStatus(source.getPayment().getStatus());
                map().setPaymentType(source.getPayment().getType());
                map().setPaymentDate(source.getPayment().getPaymentDate());
                map().setCancellationHistory(source.getCancellationHistory());
            }
        });
    }

    public BookingManagerResponseDto mapper(Booking booking) {
        return modelMapper.map(booking, BookingManagerResponseDto.class);
    }
}

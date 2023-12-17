package com.example.vhomestay.mapper;

import com.example.vhomestay.model.dto.response.FeedbackResponseDto;
import com.example.vhomestay.model.dto.response.UserResponseDto;
import com.example.vhomestay.model.entity.Customer;
import com.example.vhomestay.model.entity.Feedback;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class FeedbackCustomerResponseMapper {
    private final ModelMapper modelMapper;

    public FeedbackCustomerResponseMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        configureModelMapper();
    }

    private void configureModelMapper() {
        modelMapper.createTypeMap(Feedback.class, FeedbackResponseDto.class)
                .addMapping(Feedback::getId, FeedbackResponseDto::setId)
                .addMapping(src -> src.getHousehold().getHouseholdName(), FeedbackResponseDto::setHouseholdName)
                .addMapping(src -> src.getBooking().getTotalRoom(), FeedbackResponseDto::setTotalRoom)
                .addMapping(src -> src.getBooking().getTotalGuest(), FeedbackResponseDto::setTotalGuest)
                .addMapping(src -> src.getBooking().getTotalPrice(), FeedbackResponseDto::setTotalPrice)
                .addMapping(src -> src.getBooking().getCheckInDate(), FeedbackResponseDto::setCheckInDate)
                .addMapping(src -> src.getBooking().getCheckOutDate(), FeedbackResponseDto::setCheckOutDate)
                .addMapping(Feedback::getRating, FeedbackResponseDto::setRating)
                .addMapping(Feedback::getContent, FeedbackResponseDto::setContent);
    }

    public FeedbackResponseDto mapper(Feedback feedback) {
        return modelMapper.map(feedback, FeedbackResponseDto.class);
    }
    public Feedback mapper(FeedbackResponseDto feedbackResponseDto) {
        return modelMapper.map(feedbackResponseDto, Feedback.class);
    }
}

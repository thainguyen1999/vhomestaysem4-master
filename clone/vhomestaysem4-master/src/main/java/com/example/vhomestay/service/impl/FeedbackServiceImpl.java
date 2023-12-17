package com.example.vhomestay.service.impl;

import com.example.vhomestay.enums.FeedbackStatus;
import com.example.vhomestay.mapper.FeedbackCustomerResponseMapper;
import com.example.vhomestay.model.dto.response.FeedbackResponseDto;
import com.example.vhomestay.model.dto.response.feedback.AddFeedbackForm;
import com.example.vhomestay.model.dto.response.feedback.EditFeedbackForm;
import com.example.vhomestay.model.entity.Booking;
import com.example.vhomestay.model.entity.Customer;
import com.example.vhomestay.model.entity.Feedback;
import com.example.vhomestay.model.entity.Household;
import com.example.vhomestay.repository.BookingRepository;
import com.example.vhomestay.repository.CustomerRepository;
import com.example.vhomestay.repository.FeedbackRepository;
import com.example.vhomestay.repository.HouseholdRepository;
import com.example.vhomestay.security.SecurityUtil;
import com.example.vhomestay.service.CustomerService;
import com.example.vhomestay.service.FeedbackService;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import com.example.vhomestay.util.exception.ResourceUnauthorizedException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl extends BaseServiceImpl<Feedback, Long, FeedbackRepository>
        implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final CustomerRepository customerRepository;
    private final HouseholdRepository householdRepository;
    private final BookingRepository bookingRepository;
    private final FeedbackCustomerResponseMapper feedbackCustomerResponseMapper;

    @Override
    public List<FeedbackResponseDto> getFeedbacksByEmail() {
        List<FeedbackResponseDto> feedbackResponseDtos = new ArrayList<>();
        Optional<String> getCurrentUserEmail = SecurityUtil.getCurrentUserLogin();

        List<Feedback> feedbacks = feedbackRepository.getFeedbacksByEmail(getCurrentUserEmail.get());

        for (Feedback feedback : feedbacks) {
            feedbackResponseDtos.add(mapToDTO(feedback));
        }
        return feedbackResponseDtos;
    }

    @Override
    public Optional<FeedbackResponseDto> getFeedbackById(Long id) {
        FeedbackResponseDto feedbackResponseDto = new FeedbackResponseDto();

        Optional<String> getCurrentUserEmail = SecurityUtil.getCurrentUserLogin();

        Optional<Feedback> feedback = feedbackRepository.findById(id);
        if(feedback.isEmpty()){
            throw new ResourceNotFoundException("customer.feedback.notfound");
        }

        if(!feedback.get().getCustomer().getAccount().getEmail().equals(getCurrentUserEmail.get())){
            throw new ResourceUnauthorizedException("customer.feedback.find.denied");
        }

        feedbackResponseDto = mapToDTO(feedback.get());
        return Optional.of(feedbackResponseDto);
    }

    @Override
    public FeedbackResponseDto mapToDTO(Feedback feedback) {
        return feedbackCustomerResponseMapper.mapper(feedback);
    }

    @Override
    public Feedback mapToEntity(FeedbackResponseDto feedbackResponseDto) {
        return feedbackCustomerResponseMapper.mapper(feedbackResponseDto);
    }

    @Override
    public AddFeedbackForm showAddFeedbackForm(String bookingCode) {
        Optional<AddFeedbackForm> bookingOptional = bookingRepository.getBookingDetailByBookingCodeForAddFeedback(bookingCode);
        return bookingOptional.orElseThrow(() -> new ResourceNotFoundException("customer.booking.notfound"));
    }

    @Override
    public EditFeedbackForm showEditFeedbackForm(Long feedbackId) {
        return feedbackRepository.getBookingDetailByBookingCodeForEditFeedback(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("customer.feedback.notfound"));
    }


    @Override
    public boolean addFeedback(String bookingCode, String content, int rating) {
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        String customerEmail = currentUserLoginEmailOptional.get();
        Optional<Customer> customerOptional = customerRepository.getCustomerByAccountEmail(customerEmail);

        Optional<Booking> bookingOptional = bookingRepository.findBookingByBookingCode(bookingCode);

        Optional<Household> householdOptional = householdRepository.findById(bookingOptional.get().getHousehold().getId());

        Feedback feedback = new Feedback();
        feedback.setStatus(FeedbackStatus.SHOWED);
        feedback.setContent(content);
        feedback.setRating(rating);
        feedback.setBooking(bookingOptional.get());
        feedback.setCustomer(customerOptional.get());
        feedback.setHousehold(householdOptional.get());
        feedbackRepository.save(feedback);
        return true;
    }

    @Override
    public boolean editFeedback(Long feedbackId, String content, int rating) {
        Optional<Feedback> feedbackOptional = feedbackRepository.findById(feedbackId);
        if (feedbackOptional.isEmpty()) {
            throw new ResourceNotFoundException("customer.feedback.notfound");
        }
        Feedback feedback = feedbackOptional.get();
        feedback.setContent(content);
        feedback.setRating(rating);
        feedbackRepository.save(feedback);
        return true;
    }
}

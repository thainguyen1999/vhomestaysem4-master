package com.example.vhomestay.service.impl;

import com.example.vhomestay.config.DeployConfig;
import com.example.vhomestay.enums.BookingDetailStatus;
import com.example.vhomestay.enums.BookingStatus;
import com.example.vhomestay.model.dto.response.BookingDetailResponseDto;
import com.example.vhomestay.model.entity.*;
import com.example.vhomestay.repository.BookingDetailRepository;
import com.example.vhomestay.repository.BookingRepository;
import com.example.vhomestay.service.BookingDetailService;
import com.example.vhomestay.util.exception.ResourceInternalServerErrorException;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
public class BookingDetailServiceImpl
        extends BaseServiceImpl<BookingDetail, Long, BookingDetailRepository>
        implements BookingDetailService {
    private final BookingDetailRepository bookingDetailRepository;
    private final BookingRepository bookingRepository;
    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    @Override
    public List<BookingDetail> findBookingDetailByBookingCode(String bookingCode) {
        List<BookingDetail> bookingDetails = bookingDetailRepository.findBookingDetailByBookingCode(bookingCode);
        if (bookingDetails.isEmpty()) {
            throw new ResourceNotFoundException("manager.household.booking.detail.notfound");
        }
        return bookingDetails;
    }

    @Override
    public boolean cancelBookingDetail(List<BookingDetail> bookingDetails) {
        try {
            for (BookingDetail b : bookingDetails) {
                b.setStatus(BookingDetailStatus.CANCELLED);
                bookingDetailRepository.save(b);
            }
            return true;
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public BookingDetailResponseDto mapToDTO(BookingDetail bookingDetail) {
        List<RoomTypeFacility> roomTypeFacilities = bookingDetail.getHouseholdRoomType().getRoomTypeFacilities();
        List<String> facilityName = new ArrayList<>();

        BookingDetailResponseDto bookingDetailResponseDto = new BookingDetailResponseDto();

        bookingDetailResponseDto.setId(bookingDetail.getId());
        bookingDetailResponseDto.setHomestayCode(bookingDetail.getHomestay().getHomestayCode());
        bookingDetailResponseDto.setHomestayAddress(bookingDetail.getHomestay().getFullAddress());
        bookingDetailResponseDto.setCheckInCustomerName(bookingDetail.getCheckInCustomerName());
        bookingDetailResponseDto.setCapacityOfRoomType(bookingDetail.getHouseholdRoomType().getCapacity());
        bookingDetailResponseDto.setRoomTypeName(bookingDetail.getHouseholdRoomType().getRoomType().getRoomTypeName());
        bookingDetailResponseDto.setPrice(bookingDetail.getPrice());
        for (RoomTypeFacility r : roomTypeFacilities) {
            facilityName.add(r.getFacility().getFacilityName());
        }
        bookingDetailResponseDto.setFacilities(facilityName);
        bookingDetailResponseDto.setIsChildrenAndBed(bookingDetail.getHouseholdRoomType().getIsChildrenAndBed());
        bookingDetailResponseDto.setStatus(bookingDetail.getStatus());

        return bookingDetailResponseDto;
    }

    @Override
    public BookingDetail mapToEntity(BookingDetailResponseDto bookingDetailFormDto) {
        return null;
    }

    @Override
    public void updateBookingDetailStatusByBookingCode(String bookingCode, BookingDetailStatus status) {
        try {
            List<BookingDetail> bookingDetails = bookingDetailRepository.findBookingDetailByBookingCode(bookingCode);
            bookingDetails.forEach(bookingDetail -> bookingDetail.setStatus(status));
            bookingDetailRepository.saveAll(bookingDetails);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public void checkInBookingDetailsById(String bookingCode, List<Long> bookingDetailIds) {
        try {
            List<BookingDetail> bookingDetails = bookingDetailRepository.findAllById(bookingDetailIds);
            bookingDetails.forEach(bookingDetail -> bookingDetail.setStatus(BookingDetailStatus.CHECKED_IN));
            bookingDetailRepository.saveAll(bookingDetails);

            List<BookingDetail> bookingDetailCheck = bookingDetailRepository.findBookingDetailByBookingCode(bookingCode);
            boolean isAllBookingDetailCheckedIn = bookingDetailCheck.stream().allMatch(bookingDetail -> bookingDetail.getStatus().equals(BookingDetailStatus.CHECKED_IN));
            if (isAllBookingDetailCheckedIn) {
                Booking booking = bookingRepository.findBookingByBookingCode(bookingCode).orElseThrow(() -> new ResourceNotFoundException("manager.household.booking.notfound"));
                booking.setStatus(BookingStatus.CHECKED_IN);
                bookingRepository.save(booking);

            }
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    private void sendEmailFeedback(String bookingCode) throws MessagingException {
        Booking booking = bookingRepository.findBookingByBookingCode(bookingCode).orElseThrow(() -> new ResourceNotFoundException("booking.not.found"));

        //Customer info
        Customer customer = booking.getCustomer();
        Account account = customer.getAccount();
        String email = account.getEmail();

        //Household info
        Household household = booking.getHousehold();
        String houseName = household.getHouseholdName();

        //Link feedback
        String linkFeedback = DeployConfig.DOMAIN + "/booking/done";

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("[Làng H'Mông Pả Vi] Đánh giá của khách hàng về " + houseName);
        Context context = new Context();
        context.setVariable("customerName", customer.getFirstName() + " " + customer.getLastName());
        context.setVariable("householdName", houseName);
        context.setVariable("linkFeedback", linkFeedback);
        String html = templateEngine.process("feedback-household", context);
        helper.setText(html, true);
        emailSender.send(message);
    }

    @Override
    public void checkOutBookingDetailsById(String bookingCode, List<Long> bookingDetailIds) {
        try {
            List<BookingDetail> bookingDetails = bookingDetailRepository.findAllById(bookingDetailIds);
            bookingDetails.forEach(bookingDetail -> bookingDetail.setStatus(BookingDetailStatus.CHECKED_OUT));
            bookingDetailRepository.saveAll(bookingDetails);

            List<BookingDetail> bookingDetailCheck = bookingDetailRepository.findBookingDetailByBookingCode(bookingCode);
            boolean isAllBookingDetailCheckedOut = bookingDetailCheck.stream().allMatch(bookingDetail -> bookingDetail.getStatus().equals(BookingDetailStatus.CHECKED_OUT));
            if (isAllBookingDetailCheckedOut) {
                Booking booking = bookingRepository.findBookingByBookingCode(bookingCode).orElseThrow(() -> new ResourceNotFoundException("manager.household.booking.notfound"));
                booking.setStatus(BookingStatus.CHECKED_OUT);
                bookingRepository.save(booking);

                if(booking.getCustomer() != null){
                    sendEmailFeedback(bookingCode);
                }
            }
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }
}

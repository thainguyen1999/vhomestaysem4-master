package com.example.vhomestay.service.impl;

import com.example.vhomestay.config.DeployConfig;
import com.example.vhomestay.constant.DateTimeConstant;
import com.example.vhomestay.enums.*;
import com.example.vhomestay.mapper.BookingCustomerResponseMapper;
import com.example.vhomestay.mapper.BookingDetailManagerResponseMapper;
import com.example.vhomestay.mapper.BookingManagerResponseMapper;
import com.example.vhomestay.model.dto.request.booking.BookingCancelManagerRequestDto;
import com.example.vhomestay.model.dto.request.booking.BookingCreateManagerRequestDto;
import com.example.vhomestay.model.dto.request.booking.BookingDetailCreateManagerRequestDto;
import com.example.vhomestay.model.dto.request.booking.BookingEditManagerRequestDto;
import com.example.vhomestay.model.dto.response.BookingDetailResponseDto;
import com.example.vhomestay.model.dto.response.BookingSummaryWithDetailsDTO;
import com.example.vhomestay.model.dto.response.BookingResponseDto;
import com.example.vhomestay.model.dto.response.booking.manager.*;
import com.example.vhomestay.model.dto.response.bookingdetail.BookingDetailCheckInOutTodayResponseDto;
import com.example.vhomestay.model.dto.response.user.BookingOfCustomerDto;
import com.example.vhomestay.model.entity.*;
import com.example.vhomestay.repository.BookingDetailRepository;
import com.example.vhomestay.repository.BookingRepository;
import com.example.vhomestay.repository.CancellationHistoryRepository;
import com.example.vhomestay.repository.HomestayRepository;
import com.example.vhomestay.security.SecurityUtil;
import com.example.vhomestay.service.*;
import com.example.vhomestay.service.HouseholdService;
import com.example.vhomestay.util.exception.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class BookingServiceImpl
        extends BaseServiceImpl<Booking, Long, BookingRepository>
        implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final BookingDetailService bookingDetailService;
    private final HouseholdService householdService;
    private final HouseholdRoomTypeService householdRoomTypeService;
    private final RoomService roomService;
    private final HomestayService homestayService;
    private final DormSlotService dormSlotService;
    private final BookingForCustomerService bookingForCustomerService;
    private final CancellationReasonService cancellationReasonService;
    private final HomestayRepository homestayRepository;
    @Value("${page.size}")
    private int pageSize;
    private final BookingManagerResponseMapper bookingManagerResponseMapper;
    private final BookingDetailManagerResponseMapper bookingDetailManagerResponseMapper;
    private final BookingCustomerResponseMapper bookingCustomerResponseMapper;
    private final CancellationHistoryRepository cancellationHistoryRepository;
    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    @Override
    public List<BookingResponseDto> findBookingsByCustomerEmail() {
        List<BookingResponseDto> bookingFormDtos = new ArrayList<>();
        Optional<String> getCurrentUserEmail = SecurityUtil.getCurrentUserLogin();

        String email = getCurrentUserEmail.get();

        List<Booking> bookings = bookingRepository.findBookingsByCustomerEmail(email);

        for (Booking b : bookings) {
            BookingResponseDto bookingFormDto = mapToCustomerDTO(b);
            // Kiểm tra xem có feedback chưa
            Feedback feedback = b.getFeedback();

            // Nếu có feedback thì set isFeedbacked = true
            if (feedback != null) {
                bookingFormDto.setIsFeedbacked(true);
            } else {
                bookingFormDto.setIsFeedbacked(false);
            }

            bookingFormDtos.add(bookingFormDto);
        }
        return bookingFormDtos;
    }

    @Override
    public BookingSummaryWithDetailsDTO findBookingDetails(String bookingCode) {
        List<BookingDetailResponseDto> bookingDetailResponseDtos = new ArrayList<>();
        BookingSummaryWithDetailsDTO bookingSummaryWithDetailsDTO = new BookingSummaryWithDetailsDTO();
        Optional<String> getCurrentUserEmail = SecurityUtil.getCurrentUserLogin();

        Optional<Booking> booking = findBookingByBookingCode(bookingCode);
        if (!booking.get().getCustomer().getAccount().getEmail().equals(getCurrentUserEmail.get())) {
            throw new ResourceForbiddenException("booking.denied");
        }

        List<BookingDetail> bookingDetails = bookingDetailService.findBookingDetailByBookingCode(bookingCode);
        for (BookingDetail bd : bookingDetails) {
            bookingDetailResponseDtos.add(bookingDetailService.mapToDTO(bd));
        }

        List<BookingSummaryDto> bookingSummaryDtos = bookingDetailRepository.findBookingSummaryByBookingCode(bookingCode);

        bookingSummaryWithDetailsDTO.setBooking(mapToCustomerDTO(booking.get()));
        bookingSummaryWithDetailsDTO.setBookingSummary(bookingSummaryDtos);
        bookingSummaryWithDetailsDTO.setBookingDetails(bookingDetailResponseDtos);

        return bookingSummaryWithDetailsDTO;
    }

    @Override
    public BookingManagerSummaryWithDetailsDto findBookingDetailsByManager(String bookingCode) {
        BookingManagerSummaryWithDetailsDto bookingManagerSummaryWithDetailsDto = new BookingManagerSummaryWithDetailsDto();
        Optional<String> getCurrentUserEmail = SecurityUtil.getCurrentUserLogin();

        // Lấy ra booking theo bookingCode để kiểm tra xem có phải quản lý của booking này không
        Optional<Booking> booking = findBookingByBookingCode(bookingCode);
        if (!booking.get().getHousehold().getManager().getAccount().getEmail().equals(getCurrentUserEmail.get())) {
            throw new ResourceForbiddenException("booking.denied");
        }

        // Lấy ra danh sách bookingDetail theo bookingCode
        List<BookingDetail> bookingDetails = bookingDetailService.findBookingDetailByBookingCode(bookingCode);
        List<BookingDetailManagerResponseDto> bookingDetailManagerResponseDtos = new ArrayList<>();
        for (BookingDetail bd : bookingDetails) {
            bookingDetailManagerResponseDtos.add(bookingDetailManagerResponseMapper.mapper(bd));
        }

        // Lấy ra danh sách bookingSummary theo bookingCode
        List<BookingSummaryDto> bookingSummaryDtos = bookingDetailRepository.findBookingSummaryByBookingCode(bookingCode);

        BookingManagerResponseDto bookingManagerResponseDto = mapToManagerDTO(booking.get());
        bookingManagerResponseDto.setCreatedDate(booking.get().getCreatedDate().plusHours(DateTimeConstant.HOURS_DEPLOY));

        bookingManagerSummaryWithDetailsDto.setBookingManagerResponseDto(bookingManagerResponseDto);
        bookingManagerSummaryWithDetailsDto.setBookingSummaryDtos(bookingSummaryDtos);
        bookingManagerSummaryWithDetailsDto.setBookingDetailManagerResponseDtos(bookingDetailManagerResponseDtos);

        return bookingManagerSummaryWithDetailsDto;
    }

    @Override
    public List<BookingManagerResponseDto> findBookingsByManagerEmail(String searchValue, LocalDate checkInDate, LocalDate checkOutDate) {
        Optional<String> getCurrentUserEmail = SecurityUtil.getCurrentUserLogin();

        List<Booking> bookings = bookingRepository.findBookingsByManagerEmail(getCurrentUserEmail.get(),
                searchValue, checkInDate, checkOutDate);

        List<BookingManagerResponseDto> bookingManagerResponseDtos = new ArrayList<>();

        for (Booking b : bookings) {
            BookingManagerResponseDto bookingManagerResponseDto = bookingManagerResponseMapper.mapper(b);
            bookingManagerResponseDto.setCreatedDate(b.getCreatedDate().plusHours(DateTimeConstant.HOURS_DEPLOY));
            bookingManagerResponseDtos.add(bookingManagerResponseDto);
        }
        return bookingManagerResponseDtos;
    }

    @Override
    public Optional<Booking> findBookingByBookingCode(String bookingCode) {
        Optional<Booking> booking = bookingRepository.findBookingByBookingCode(bookingCode);
        if (booking.isEmpty()) {
            throw new ResourceNotFoundException("booking.notfound");
        }
        return booking;
    }

    @Override
    @Transactional
    public boolean cancelBookingByManager(BookingCancelManagerRequestDto bookingCancelManagerRequestDto) {
        // Lấy ra booking theo bookingCode
        String bookingCode = bookingCancelManagerRequestDto.getBookingCode();
        Optional<Booking> bookingOptional = findBookingByBookingCode(bookingCode);

        Booking booking = bookingOptional.get();
        if (!booking.getStatus().equals(BookingStatus.BOOKED)) {
            throw new ResourceNotFoundException("booking.booked.failed");
        }

        // Gán trạng thái booking thành CANCELLED
        booking.setStatus(BookingStatus.CANCELLED);

        // Chuyển trạng thái bookingDetail thành CANCELLED
        List<BookingDetail> bookingDetails = bookingDetailService.findBookingDetailByBookingCode(bookingCode);
        for (BookingDetail bd : bookingDetails) {
            bd.setStatus(BookingDetailStatus.CANCELLED);
        }

        booking.setBookingDetails(bookingDetails);

        // Lưu lịch sử hủy booking
        CancellationHistory cancellationHistory = new CancellationHistory();
        cancellationHistory.setCancellationDate(LocalDateTime.now());
        cancellationHistory.setCancellationReason(bookingCancelManagerRequestDto.getCancelReason());
        cancellationHistory.setRefundStatus(bookingCancelManagerRequestDto.getRefundStatus());

        // Nếu trạng thái hoàn tiền là REFUNDED thì set ngày hoàn tiền là ngày hiện tại
//        if (bookingCancelManagerRequestDto.getRefundStatus().equals(RefundStatus.REFUNDED)) {
//            cancellationHistory.setRefundDate(LocalDateTime.now());
//        } else if (bookingCancelManagerRequestDto.getRefundStatus().equals(RefundStatus.NOT_REFUNDED)) {
//            cancellationHistory.setRefundDate(null);
//        }

        // Kiểm tra xem booking có được hoàn tiền hay không
        LocalDate cancellationDate = LocalDate.now();

        int cancellationPeriod = booking.getHousehold().getCancellationPeriod();
        LocalDate cancellationDeadline = booking.getCheckInDate().minusDays(cancellationPeriod);

        // Nếu hủy sau thời hạn hủy thì không hoàn tiền
        if (cancellationDate.isAfter(cancellationDeadline)) {
            System.out.println("Hủy sau thời hạn hủy");
            cancellationHistory.setRefundAmount(BigDecimal.ZERO);
            cancellationHistory.setRefundDate(null);
            cancellationHistory.setRefundStatus(RefundStatus.NOT_REFUNDED);
        } else {
            cancellationHistory.setRefundAmount(booking.getTotalPrice());
            cancellationHistory.setRefundDate(LocalDateTime.now());
            cancellationHistory.setRefundStatus(RefundStatus.REFUNDED);
        }

        System.out.println("Refund amount: " + cancellationHistory.getRefundAmount());
        cancellationHistory.setCustomer(booking.getCustomer());
        cancellationHistory.setBooking(booking);

        booking.setCancellationHistory(cancellationHistory);

        try {
            bookingRepository.save(booking);
            return true;
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public boolean cancelBooking(Booking booking) {
        try {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            return true;
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public BookingResponseDto mapToCustomerDTO(Booking booking) {
        return bookingCustomerResponseMapper.mapToCustomerDTO(booking);
    }

    @Override
    public BookingManagerResponseDto mapToManagerDTO(Booking booking) {
        return bookingManagerResponseMapper.mapper(booking);
    }

    @Override
    public Booking mapToCustomerEntity(BookingResponseDto bookingResponseDto) {
        return bookingCustomerResponseMapper.mapToCustomerEntity(bookingResponseDto);
    }

    @Override
    public BookingCancelManagerResponseDto findBookingCancelFormByManager(String bookingCode) {
        Optional<String> getCurrentUserEmail = SecurityUtil.getCurrentUserLogin();
        Optional<Booking> bookingOptional = findBookingByBookingCode(bookingCode);

        // Kiểm tra xem có phải quản lý của booking này không
        if (!bookingOptional.get().getHousehold().getManager().getAccount().getEmail().equals(getCurrentUserEmail.get())) {
            throw new ResourceForbiddenException("booking.denied");
        }

        LocalDate cancellationDate = LocalDate.now();

        // Kiểm tra xem có hủy sau thời hạn hủy không
        int cancellationPeriod = bookingOptional.get().getHousehold().getCancellationPeriod();
        LocalDate cancellationDeadline = bookingOptional.get().getCheckInDate().minusDays(cancellationPeriod);

        // Tính toán số tiền hoàn lại
        BigDecimal refundAmount = bookingOptional.get().getTotalPrice();

        // Nếu hủy sau thời hạn hủy thì không hoàn tiền
        if (cancellationDate.isAfter(cancellationDeadline)) {
            refundAmount = new BigDecimal("0");
        }

        // Lấy ra danh sách lý do hủy
        List<CancellationReason> cancellationReasons = cancellationReasonService.findAll();

        // Lấy ra danh sách trạng thái hoàn tiền
        List<RefundStatus> refundStatuses = new ArrayList<>();
        for (RefundStatus rs : RefundStatus.values()) {
            refundStatuses.add(rs);
        }

        BookingCancelManagerResponseDto bookingCancelManagerResponseDto = new BookingCancelManagerResponseDto();
        bookingCancelManagerResponseDto.setBookingCode(bookingCode);
        bookingCancelManagerResponseDto.setRefundAmount(refundAmount);
        bookingCancelManagerResponseDto.setCancellationReasons(cancellationReasons);
        bookingCancelManagerResponseDto.setRefundStatuses(refundStatuses);
        bookingCancelManagerResponseDto.setCancellationPeriod(cancellationPeriod);

        return bookingCancelManagerResponseDto;
    }

    @Override
    public void updateBookingByManager(BookingEditManagerRequestDto bookingEditManagerRequestDto) {
        try {
            String bookingCode = bookingEditManagerRequestDto.getBookingCode();
            Optional<Booking> bookingOptional = findBookingByBookingCode(bookingCode);
            Booking booking = bookingOptional.get();

            booking.setCheckInName(bookingEditManagerRequestDto.getBookingCustomerName());
            booking.setCheckInPhoneNumber(bookingEditManagerRequestDto.getBookingCustomerPhoneNumber());

            List<BookingDetail> bookingDetails = bookingDetailService.findBookingDetailByBookingCode(bookingCode);
            for (BookingDetail bd : bookingDetails) {
                bd.setCheckInCustomerName(bookingEditManagerRequestDto.getBookingCustomerName());
                bd.setBooking(booking);
            }

            booking.setBookingDetails(bookingDetails);
            bookingRepository.save(booking);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public String findHouseNameByBookingCode(String bookingCode) {
        return bookingRepository.findHouseNameByBookingCode(bookingCode);
    }

    @Override
    public void sendEmailBookingSuccess(String bookingCode) throws MessagingException {
        Optional<Booking> bookingOptional = findBookingByBookingCode(bookingCode);
        Booking booking = bookingOptional.get();
        String linkViewBooking = DeployConfig.DOMAIN + "/booking/detail/" + bookingCode;

        //Customer info
        Customer customer = booking.getCustomer();
        Account account = customer.getAccount();
        String email = account.getEmail();

        //Household info
        Household household = booking.getHousehold();
        String houseName = household.getHouseholdName();
        String phoneNumber = "+84 " + household.getPhoneNumberFirst().trim().substring(1);
        String linkCall = "tel://" + phoneNumber;
        String emailHousehold = household.getEmail();
        String linkEmail = "mailto:" + emailHousehold;
        List<String> address = homestayRepository.getAddress(household.getId());

        //Booking info
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, 'ngày' dd 'tháng' MM 'năm' yyyy", new Locale("vi"));
        String checkInDateBooking = booking.getCheckInDate().format(formatter);
        String checkOutDateBooking = booking.getCheckOutDate().format(formatter);
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd 'tháng' MM, yyyy", new Locale("vi"));
        String dateCancelBookingAllowed = booking.getCheckInDate().minusDays(household.getCancellationPeriod()).format(formatterDate);
        String dateCancelBookingNotAllowed = booking.getCheckInDate().minusDays(household.getCancellationPeriod() - 1).format(formatterDate);
        DecimalFormat formatterPrice = new DecimalFormat("#,###");
        String totalPriceBooking = formatterPrice.format(booking.getTotalPrice()) + "VND";


        //Payment info
        Payment payment = booking.getPayment();
        String paymentDate = payment.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("Đặt phòng của bạn ở " + houseName + " đã được xác nhận");
        Context context = new Context();
        context.setVariable("booking", booking);
        context.setVariable("customer", customer);
        context.setVariable("household", household);
        context.setVariable("addressHousehold", address);
        context.setVariable("phoneNumber", phoneNumber);
        context.setVariable("linkCall", linkCall);
        context.setVariable("emailHousehold", emailHousehold);
        context.setVariable("linkEmail", linkEmail);
        context.setVariable("checkInDate", checkInDateBooking);
        context.setVariable("checkOutDate", checkOutDateBooking);
        context.setVariable("paymentDate", paymentDate);
        context.setVariable("dateCancelBookingAllowed", dateCancelBookingAllowed);
        context.setVariable("dateCancelBookingNotAllowed", dateCancelBookingNotAllowed);
        context.setVariable("totalPrice", totalPriceBooking);
        context.setVariable("linkViewBooking", linkViewBooking);
        context.setVariable("linkWebsite", DeployConfig.DOMAIN);
        context.setVariable("linkFacebook", DeployConfig.FACEBOOK_LINK);
        context.setVariable("linkYoutube", DeployConfig.YOUTUBE_LINK);
        context.setVariable("linkTiktok", DeployConfig.TIKTOK_LINK);
        String html = templateEngine.process("email-confirm-booking", context);
        helper.setText(html, true);
        emailSender.send(message);
    }

    @Override
    public void updateBookingStatusByBookingCode(String bookingCode, BookingStatus status) {
        try {
            Booking booking = findBookingByBookingCode(bookingCode).get();
            booking.setStatus(status);
            bookingRepository.save(booking);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public boolean checkInBookingByManager(String bookingCode) {
        String emailManager = SecurityUtil.getCurrentUserLogin().get();

        Booking booking = findBookingByBookingCode(bookingCode).get();


        // Kiểm tra xem booking có phải là đang chờ check in không
        if (booking.getStatus() != BookingStatus.BOOKED) {
            // TODO: Cần kiểm tra thêm xem booking này có thuộc booking đang chờ check in ngày hôm nay không
            throw new ResourceNotFoundException("booking.booked.failed");
        }

        List<BookingDetail> bookingDetails = bookingDetailService.findBookingDetailByBookingCode(bookingCode);
        for (BookingDetail bookingDetail : bookingDetails) {
            bookingDetail.setStatus(BookingDetailStatus.CHECKED_IN);
        }

        try {
            booking.setBookingDetails(bookingDetails);
            booking.setStatus(BookingStatus.CHECKED_IN);
            bookingRepository.save(booking);
            return true;
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
    public boolean checkOutBookingByManager(String bookingCode) {
        String emailManager = SecurityUtil.getCurrentUserLogin().get();
        Booking booking = findBookingByBookingCode(bookingCode).get();

        if (!booking.getHousehold().getManager().getAccount().getEmail().equals(emailManager)) {
            throw new ResourceForbiddenException("booking.manager.not.belong");
        }

        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            // TODO: Cần kiểm tra thêm xem booking này có thuộc booking đang chờ check in ngày hôm nay không
            throw new ResourceNotFoundException("booking.check.in.failed");
        }

        List<BookingDetail> bookingDetails = bookingDetailService.findBookingDetailByBookingCode(bookingCode);
        for (BookingDetail bookingDetail : bookingDetails) {
            bookingDetail.setStatus(BookingDetailStatus.CHECKED_OUT);
        }

        try {
            booking.setBookingDetails(bookingDetails);
            booking.setStatus(BookingStatus.CHECKED_OUT);
            bookingRepository.save(booking);

            if (booking.getCustomer() != null) {
                sendEmailFeedback(bookingCode);
            }
            return true;
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public List<BookingOfCustomerDto> getBookingOfCustomer(Long accountId) {
        return bookingRepository.getBookingOfCustomer(accountId);
    }

//    @Override
//    public List<BookingCheckInOutTodayResponseDto> findBookingsCheckInOutTodayByManager() {
//        String emailManager = SecurityUtil.getCurrentUserLogin().get();
//
//        List<Booking> bookings = bookingRepository.findBookingsCheckInOutTodayByManager(emailManager);
//        List<BookingCheckInOutTodayResponseDto> bookingCheckInOutTodayResponseDtos = new ArrayList<>();
//        List<BookingDetail> bookingDetails = new ArrayList<>();
//        BookingCheckInOutTodayResponseDto bookingCheckInOutTodayResponseDto;
//        BookingDetailCheckInOutTodayResponseDto bookingDetailCheckInOutTodayResponseDto;
//
//        for (Booking booking : bookings) {
//            bookingCheckInOutTodayResponseDto = new BookingCheckInOutTodayResponseDto();
//
//            bookingCheckInOutTodayResponseDto.setBookingCode(booking.getBookingCode());
//            bookingCheckInOutTodayResponseDto.setBookingCustomerName(booking.getCheckInName());
//            bookingCheckInOutTodayResponseDto.setBookingCustomerPhone(booking.getCheckInPhoneNumber());
//
//            bookingDetails = bookingDetailService.findBookingDetailByBookingCode(booking.getBookingCode());
//
//            bookingCheckInOutTodayResponseDto.setBookingDetailCheckInOutTodayResponseDtos(new ArrayList<>());
//
//            for (BookingDetail bookingDetail : bookingDetails) {
//                bookingDetailCheckInOutTodayResponseDto = new BookingDetailCheckInOutTodayResponseDto();
//
//                bookingDetailCheckInOutTodayResponseDto.setRoomName(bookingDetail.getRoom().getRoomName());
//                bookingDetailCheckInOutTodayResponseDto.setRoomTypeName(bookingDetail.getRoom().getHouseholdRoomType().getRoomType().getRoomTypeName());
//                bookingDetailCheckInOutTodayResponseDto.setBookingDetailStatus(bookingDetail.getStatus());
//
//                if (bookingDetail.getDormSlot() != null) {
//                    DormSlot dormSlot = dormSlotService.findDormSlotById(bookingDetail.getDormSlot().getId()).get();
//
//                    DormSlotFormDto dormSlotFormDto = new DormSlotFormDto();
//                    dormSlotFormDto.setRoomId(dormSlot.getRoom().getId());
//                    dormSlotFormDto.setNumberOfSlots(dormSlot.getSlotNumber());
//
//                    bookingDetailCheckInOutTodayResponseDto.setDormSlotFormDtos(new ArrayList<>());
//                    bookingDetailCheckInOutTodayResponseDto.getDormSlotFormDtos().add(dormSlotFormDto);
//                }
//
//                bookingCheckInOutTodayResponseDto.getBookingDetailCheckInOutTodayResponseDtos().add(bookingDetailCheckInOutTodayResponseDto);
//            }
//
//            bookingCheckInOutTodayResponseDtos.add(bookingCheckInOutTodayResponseDto);
//        }
//
//        return bookingCheckInOutTodayResponseDtos;
//    }

    @Override
    public List<BookingCheckInOutTodayResponseDto> findBookingsCheckInTodayByManager(LocalDate checkInDate, Long homestayId) {
        String emailManager = SecurityUtil.getCurrentUserLogin().get();

        // Lấy danh sách booking đang chờ check in
        List<Booking> bookingsCheckIn = bookingRepository.findBookingsCheckInByManager(emailManager, checkInDate);


        List<BookingCheckInOutTodayResponseDto> bookingCheckInOutTodayResponseDtos = new ArrayList<>();
        BookingCheckInOutTodayResponseDto bookingCheckInOutTodayResponseDto;

        List<BookingDetailCheckInOutTodayResponseDto> bookingDetailCheckInOutTodayResponseDtos = new ArrayList<>();
        BookingDetailCheckInOutTodayResponseDto bookingDetailCheckInOutTodayResponseDto;

        for (Booking b : bookingsCheckIn) {
            bookingCheckInOutTodayResponseDto = new BookingCheckInOutTodayResponseDto();
            bookingCheckInOutTodayResponseDto.setBookingCode(b.getBookingCode());
            bookingCheckInOutTodayResponseDto.setBookingCustomerName(b.getCheckInName());
            bookingCheckInOutTodayResponseDto.setBookingCustomerPhone(b.getCheckInPhoneNumber());
            bookingCheckInOutTodayResponseDto.setBookingStatus(b.getStatus());

            if (b.getCustomer() != null) {
                bookingCheckInOutTodayResponseDto.setBookingCustomerEmail(b.getCustomer().getAccount().getEmail());
            }

            bookingDetailCheckInOutTodayResponseDtos = bookingDetailRepository.findBookingDetailCheckInOut(b.getBookingCode());

            if (homestayId != null) {
                bookingDetailCheckInOutTodayResponseDtos.removeIf(bookingDetail -> !bookingDetail.getHomestayId().equals(homestayId));
            }

            bookingCheckInOutTodayResponseDto.setBookingDetailCheckInOutTodayResponseDtos(bookingDetailCheckInOutTodayResponseDtos);

            bookingCheckInOutTodayResponseDtos.add(bookingCheckInOutTodayResponseDto);
        }
        return bookingCheckInOutTodayResponseDtos;
    }

    @Override
    public List<BookingCheckInOutTodayResponseDto> findBookingsCheckOutTodayByManager(LocalDate checkOutToday, Long homestayId) {
        String emailManager = SecurityUtil.getCurrentUserLogin().get();

        // Lấy danh sách booking đang chờ check out
        List<Booking> bookingsCheckOut = bookingRepository.findBookingsCheckOutByManager(emailManager, checkOutToday);

        List<BookingCheckInOutTodayResponseDto> bookingCheckInOutTodayResponseDtos = new ArrayList<>();
        BookingCheckInOutTodayResponseDto bookingCheckInOutTodayResponseDto;

        List<BookingDetailCheckInOutTodayResponseDto> bookingDetailCheckInOutTodayResponseDtos = new ArrayList<>();
        BookingDetailCheckInOutTodayResponseDto bookingDetailCheckInOutTodayResponseDto;

        for (Booking b : bookingsCheckOut) {
            bookingCheckInOutTodayResponseDto = new BookingCheckInOutTodayResponseDto();
            bookingCheckInOutTodayResponseDto.setBookingCode(b.getBookingCode());
            bookingCheckInOutTodayResponseDto.setBookingCustomerName(b.getCheckInName());
            bookingCheckInOutTodayResponseDto.setBookingCustomerPhone(b.getCheckInPhoneNumber());
            bookingCheckInOutTodayResponseDto.setBookingStatus(b.getStatus());

            if (b.getCustomer() != null) {
                bookingCheckInOutTodayResponseDto.setBookingCustomerEmail(b.getCustomer().getAccount().getEmail());
            }

            bookingDetailCheckInOutTodayResponseDtos = bookingDetailRepository.findBookingDetailCheckInOut(b.getBookingCode());

            if (homestayId != null) {
                bookingDetailCheckInOutTodayResponseDtos.removeIf(bookingDetail -> !bookingDetail.getHomestayId().equals(homestayId));
            }

            bookingCheckInOutTodayResponseDto.setBookingDetailCheckInOutTodayResponseDtos(bookingDetailCheckInOutTodayResponseDtos);

            bookingCheckInOutTodayResponseDtos.add(bookingCheckInOutTodayResponseDto);
        }
        return bookingCheckInOutTodayResponseDtos;
    }

    @Override
    public List<BookingDetailManagerResponseDto> getBookingDetailCheckInOutByManager(String bookingCode) {
        List<BookingDetail> bookingDetails = bookingDetailRepository.findBookingDetailByBookingCode(bookingCode);
        List<BookingDetailManagerResponseDto> bookingDetailManagerResponseDtos = new ArrayList<>();
        BookingDetailManagerResponseDto bookingDetailManagerResponseDto;

        for (BookingDetail bd : bookingDetails) {
            bookingDetailManagerResponseDto = new BookingDetailManagerResponseDto();
            bookingDetailManagerResponseDto.setId(bd.getId());
            bookingDetailManagerResponseDto.setBookingCode(bd.getBooking().getBookingCode());
            bookingDetailManagerResponseDto.setHomestayCode(bd.getHomestay().getHomestayCode());
            bookingDetailManagerResponseDto.setRoomTypeName(bd.getRoom().getHouseholdRoomType().getRoomType().getRoomTypeName());
            bookingDetailManagerResponseDto.setRoomName(bd.getRoom().getRoomName());
            if (bd.getHouseholdRoomType().getRoomType().getIsDorm()) {
                bookingDetailManagerResponseDto.setSlotNumber(bd.getDormSlot().getSlotNumber());
            }
            bookingDetailManagerResponseDto.setCheckInCustomerName(bd.getCheckInCustomerName());
            bookingDetailManagerResponseDto.setBookingDetailStatus(bd.getStatus());
            bookingDetailManagerResponseDto.setBookingStatus(bd.getBooking().getStatus());
            bookingDetailManagerResponseDto.setIsDorm(bd.getRoom().getHouseholdRoomType().getRoomType().getIsDorm());

            bookingDetailManagerResponseDtos.add(bookingDetailManagerResponseDto);
        }
        return bookingDetailManagerResponseDtos;
    }

    @Override
    public void refundBookingByManager(String bookingCode) {
        String emailManager = SecurityUtil.getCurrentUserLogin().get();

        CancellationHistory cancellationHistory = cancellationHistoryRepository.findCancellationHistoryByBookingCodeAndManagerEmail(bookingCode, emailManager)
                .orElseThrow(() -> new ResourceNotFoundException("cancellation.history.not.found"));

        if (cancellationHistory.getRefundStatus().equals(RefundStatus.PENDING)) {
            cancellationHistory.setRefundStatus(RefundStatus.REFUNDED);
        }

        try {
            cancellationHistoryRepository.save(cancellationHistory);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }

    }

    @Override
    @Transactional
    public void bookingRoomByManager(BookingCreateManagerRequestDto bookingCreateManagerRequestDto) {
        // Lấy thông tin household
        Household household = householdService.getHouseholdByManagerEmail().orElseThrow(() -> new ResourceNotFoundException("household.not.found"));

        String bookingCode = bookingForCustomerService.generateBookingCode(0L,
                bookingCreateManagerRequestDto.getHouseholdId());

        int totalNight = (int) ChronoUnit.DAYS.between(bookingCreateManagerRequestDto.getCheckInDate(), bookingCreateManagerRequestDto.getCheckOutDate());

        // Lưu booking
        Booking booking = new Booking();
        booking.setBookingCode(bookingCode);
        booking.setTotalRoom(bookingCreateManagerRequestDto.getTotalRoom());
        booking.setCheckInDate(bookingCreateManagerRequestDto.getCheckInDate());
        booking.setCheckOutDate(bookingCreateManagerRequestDto.getCheckOutDate());
        booking.setTotalGuest(bookingCreateManagerRequestDto.getTotalOfGuest());
        booking.setTotalPrice(bookingCreateManagerRequestDto.getTotalPrice());
        booking.setCheckInName(bookingCreateManagerRequestDto.getCheckInCustomerName());
        booking.setCheckInPhoneNumber(bookingCreateManagerRequestDto.getCheckInCustomerPhone());
        booking.setTotalNight(totalNight);
        booking.setStatus(BookingStatus.BOOKED);
        booking.setHousehold(household);

        // Lấy thông tin booking detail
        List<BookingDetailCreateManagerRequestDto> bookingDetailCreateManagerRequestDtos = bookingCreateManagerRequestDto.getBookingDetails();
        List<BookingDetail> bookingDetails = new ArrayList<>();

        HouseholdRoomType householdRoomType;
        Room room;
        Homestay homestay;
        DormSlot dormSlot;
        BookingDetail bookingDetail;

        for (BookingDetailCreateManagerRequestDto bookingDetailCreateManagerRequestDto : bookingDetailCreateManagerRequestDtos) {
            bookingDetail = new BookingDetail();
            bookingDetail.setCheckInCustomerName(booking.getCheckInName());
            bookingDetail.setPrice(bookingDetailCreateManagerRequestDto.getPrice());
            bookingDetail.setStatus(BookingDetailStatus.BOOKED);
            bookingDetail.setSubTotal(bookingDetailCreateManagerRequestDto.getPrice().multiply(BigDecimal.valueOf(totalNight)));
            bookingDetail.setBooking(booking);

            // Lấy thông tin dormslot
            if (bookingDetailCreateManagerRequestDto.getIsDorm()) {
                dormSlot = new DormSlot();
                dormSlot = dormSlotService.findById(bookingDetailCreateManagerRequestDto.getDormSlotId()).get();
            } else {
                dormSlot = null;
            }
            bookingDetail.setDormSlot(dormSlot);

            // Lấy thông tin homestay
            homestay = new Homestay();
            homestay = homestayService.findById(bookingDetailCreateManagerRequestDto.getHomestayId()).get();
            bookingDetail.setHomestay(homestay);

            // Lấy thông tin household room type
            householdRoomType = new HouseholdRoomType();
            householdRoomType = householdRoomTypeService.findById(bookingDetailCreateManagerRequestDto.getHouseholdRoomTypeId()).get();
            bookingDetail.setHouseholdRoomType(householdRoomType);

            // Lấy thông tin room
            room = new Room();
            room = roomService.findById(bookingDetailCreateManagerRequestDto.getRoomId()).get();
            bookingDetail.setRoom(room);


            bookingDetails.add(bookingDetail);
        }

        booking.setBookingDetails(bookingDetails);

        // Lưu bảng payment
        Payment payment = new Payment();
        payment.setAmount(bookingCreateManagerRequestDto.getTotalPrice());
        payment.setBooking(booking);
        payment.setStatus(PaymentStatus.UNPAID);
        payment.setType(PaymentType.OUTSIDE_SYSTEM);

        booking.setPayment(payment);

        try {
            bookingRepository.save(booking);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

}

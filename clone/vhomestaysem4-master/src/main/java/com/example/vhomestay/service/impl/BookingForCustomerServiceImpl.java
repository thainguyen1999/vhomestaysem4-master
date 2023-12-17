package com.example.vhomestay.service.impl;

import com.example.vhomestay.config.DeployConfig;
import com.example.vhomestay.constant.DateTimeConstant;
import com.example.vhomestay.enums.*;
import com.example.vhomestay.mapper.BookingCreateCustomerRequestMapper;
import com.example.vhomestay.mapper.BookingDetailRecommendResponseMapper;
import com.example.vhomestay.mapper.RoomTypeHouseholdForBookingResponseMapper;
import com.example.vhomestay.model.dto.request.NotificationBookingCancelRequest;
import com.example.vhomestay.model.dto.request.booking.BookingCancelCustomerRequestDto;
import com.example.vhomestay.model.dto.request.booking.BookingCreateCustomerRequestDto;
import com.example.vhomestay.model.dto.request.booking.BookingDetailCreateCustomerRequestDto;
import com.example.vhomestay.model.dto.request.booking.BookingInfoForUpdateDto;
import com.example.vhomestay.model.dto.response.HomestayDto;
import com.example.vhomestay.model.dto.response.booking.customer.*;
import com.example.vhomestay.model.dto.response.service.customer.ServiceResponse;
import com.example.vhomestay.model.entity.*;
import com.example.vhomestay.repository.*;
import com.example.vhomestay.security.SecurityUtil;
import com.example.vhomestay.service.*;
import com.example.vhomestay.util.exception.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookingForCustomerServiceImpl extends BaseServiceImpl<BookingDetail, Long, BookingDetailRepository>
        implements BookingForCustomerService {

    private final CustomerRepository customerRepository;
    private final HouseholdRepository householdRepository;
    private final HomestayRepository homestayRepository;
    private final HouseholdRoomTypeRepository householdRoomTypeRepository;
    private final FeedbackRepository feedbackRepository;
    private final HouseholdServiceRepository householdServiceRepository;
    private final ServiceRepository serviceRepository;
    private final HomestayMediaRepository homestayMediaRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final RoomTypeFacilityRepository roomTypeFacilityRepository;
    private final BookingRepository bookingRepository;
    private final BookingDetailService bookingDetailService;
    private final CancellationReasonService cancellationReasonService;
    private final CancellationHistoryService cancellationHistoryService;
    private final CustomerBankInformationService customerBankInformationService;
    private final RoomTypeHouseholdForBookingResponseMapper roomTypeHouseholdForBookingResponseMapper;
    private final BookingDetailRecommendResponseMapper bookingDetailRecommendResponseMapper;
    private final BookingCreateCustomerRequestMapper bookingCreateCustomerRequestMapper;
    private final AccountRepository accountRepository;
    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    private final NotificationService notificationService;
    private final ContactUsRepository contactUsRepository;

    private HouseholdDto setHouseholdInfo(Household household) {
        HouseholdDto householdDto = new HouseholdDto();
        householdDto.setHouseholdId(household.getId());
        householdDto.setHouseholdName(household.getHouseholdName());
        householdDto.setImageUri(household.getAvatar());
        householdDto.setAddress(homestayRepository.getAddress(household.getId()));
        householdDto.setHaveDormitory(householdRoomTypeRepository.findDormitoryByHouseholdId(household.getId()).size() > 0);
        householdDto.setRating(feedbackRepository.getRatingAverageByHouseholdId(household.getId()));
        householdDto.setNumberOfReviews(feedbackRepository.countFeedbackByHousehold(household.getId()));
        householdDto.setHouseholdServiceList(householdServiceRepository.getAllHouseholdServiceByHouseholdId(household.getId()));
        return householdDto;
    }

    private HouseholdForBookingResponseDto setHouseholdForBookingInfo(Household household) {
        HouseholdForBookingResponseDto householdDto = new HouseholdForBookingResponseDto();
        householdDto.setHouseholdId(household.getId());
        householdDto.setHouseholdName(household.getHouseholdName());
        householdDto.setHouseholdDescription(household.getDescription());
        householdDto.setPhoneNumber1(household.getPhoneNumberFirst());
        householdDto.setPhoneNumber2(household.getPhoneNumberSecond());
        householdDto.setImageUri(household.getAvatar());
        householdDto.setCheckInTime(String.valueOf(household.getCheckInTime()));
        householdDto.setCheckOutTime(String.valueOf(household.getCheckOutTime()));
        householdDto.setAddress(homestayRepository.getAddress(household.getId()));
        householdDto.setHaveDormitory(householdRoomTypeRepository.findDormitoryByHouseholdId(household.getId()).size() > 0);
        householdDto.setRating(feedbackRepository.getRatingAverageByHouseholdId(household.getId()));
        householdDto.setNumberOfReviews(feedbackRepository.countFeedbackByHousehold(household.getId()));
        householdDto.setReviewHouseholdList(feedbackRepository.getFeedbackByHouseholdId(household.getId()));
        householdDto.setHouseholdServiceList(householdServiceRepository.getAllHouseholdServiceByHouseholdId(household.getId()));
        return householdDto;
    }

    private List<HomestayAndTypeRoomAvailableDto> roomAvailableList(Long householdId, LocalDate checkInDate, LocalDate checkOutDate) {
        List<HomestayAndTypeRoomAvailableDto> homestayAndTypeRoomAvailableDtoList = new ArrayList<>();
        List<HomestayDto> homestayList = homestayRepository.findAllByHouseholdId(householdId);
        if (homestayList == null) {
            throw new ResourceNotFoundException("homestay.not.found");
        }
        for(HomestayDto homestayDto : homestayList) {
            HomestayAndTypeRoomAvailableDto homestayAndTypeRoomAvailableDto = new HomestayAndTypeRoomAvailableDto();
            homestayAndTypeRoomAvailableDto.setHomestayId(homestayDto.getId());
            homestayAndTypeRoomAvailableDto.setHomestayCode(homestayDto.getHomestayCode());
            homestayAndTypeRoomAvailableDto.setImageUriList(homestayMediaRepository.findImageUriByHomestayId(homestayDto.getId()));
            List<RoomTypeHouseholdAvailableWithFullInfoDto> roomTypeHouseholdAvailableDtoList = getRoomTypeAvailableByHomestay(homestayDto.getId(), checkInDate, checkOutDate);
            homestayAndTypeRoomAvailableDto.setRoomTypeAvailableList(roomTypeHouseholdAvailableDtoList);
            if (roomTypeHouseholdAvailableDtoList != null) {
                int capacityAvailable = roomTypeHouseholdAvailableDtoList.stream()
                        .mapToInt(dto -> dto.getCapacity() * Math.toIntExact(dto.getQuantity()))
                        .sum();
                homestayAndTypeRoomAvailableDto.setCapacityAvailable(capacityAvailable);
            } else {
                homestayAndTypeRoomAvailableDto.setCapacityAvailable(0);
            }
            homestayAndTypeRoomAvailableDtoList.add(homestayAndTypeRoomAvailableDto);
        }
        return homestayAndTypeRoomAvailableDtoList;
    }

    private List<RoomTypeHouseholdAvailableWithFullInfoDto> getRoomTypeAvailableByHomestay(Long homestayId, LocalDate checkInDate, LocalDate checkOutDate) {
        //List all roomType with isDormitory = false
        List<RoomTypeHouseholdAvailableDto> roomTypeHouseholdAvailableDtoList = bookingDetailRepository.getRoomTypeAvailableByHomestay(homestayId, checkInDate, checkOutDate);
        //List roomType with isDormitory = true
        List<RoomTypeHouseholdAvailableDto> dormitoryAvailableDto = bookingDetailRepository.getDormitoryAvailableByHomestay(homestayId, checkInDate, checkOutDate);
        //Add dormitoryAvailableDto to first index of roomTypeHouseholdAvailableDtoList
        if (dormitoryAvailableDto != null) {
            roomTypeHouseholdAvailableDtoList.addAll(0, dormitoryAvailableDto);
        }
        List<RoomTypeHouseholdAvailableWithFullInfoDto> roomTypeHouseholdAvailableWithFullInfoDtoList = roomTypeHouseholdForBookingResponseMapper.mapper(roomTypeHouseholdAvailableDtoList);
        if(roomTypeHouseholdAvailableWithFullInfoDtoList != null) {
            roomTypeHouseholdAvailableWithFullInfoDtoList.forEach(roomTypeHouseholdAvailableWithFullInfoDto -> {
                Long id = roomTypeHouseholdAvailableWithFullInfoDto.getRoomTypeHouseholdId();
                roomTypeHouseholdAvailableWithFullInfoDto.setImageListUri(homestayMediaRepository.findImageUriByRoomTypeId(id));
                roomTypeHouseholdAvailableWithFullInfoDto.setFacilities(roomTypeFacilityRepository.findFacilityByRoomTypeId(id));
            });
        }
        return roomTypeHouseholdAvailableWithFullInfoDtoList;
    }

    private List<BookingDetailRecommendDto> bookingDetailRecommendList(List<HomestayAndTypeRoomAvailableDto> listRoomAvailable, Integer numberOfGuests) {
        List<BookingDetailRecommendDto> bookingDetailRecommendDtoList = new ArrayList<>();
        int capacityAvailable = listRoomAvailable.stream()
                .mapToInt(HomestayAndTypeRoomAvailableDto::getCapacityAvailable)
                .sum();
        if (capacityAvailable < numberOfGuests) {
            return Collections.emptyList();
        }
        
        listRoomAvailable.sort(Comparator.comparing(HomestayAndTypeRoomAvailableDto::getCapacityAvailable));
        final int guestFinal = numberOfGuests;
        HomestayAndTypeRoomAvailableDto dto = listRoomAvailable.stream()
                .filter(d -> d.getCapacityAvailable() >= guestFinal)
                .findFirst()
                .orElse(null);
        if (dto == null) {
            Collections.reverse(listRoomAvailable);
        }

        int quantityOfRoomType;
        int quantityTemp;
        int check;
        int capacityTemp = 0;
        int remainingRoomTypeCapacity;

        for (HomestayAndTypeRoomAvailableDto currentDto : listRoomAvailable) {
            if (dto != null && currentDto != dto) {
                continue;
            }
            List<RoomTypeHouseholdAvailableWithFullInfoDto> roomTypeAvailableList = currentDto.getRoomTypeAvailableList();
            for (int i = roomTypeAvailableList.size() - 1; i >= 0; i--) {
                RoomTypeHouseholdAvailableWithFullInfoDto roomTypeHouseholdAvailableWithFullInfoDto = roomTypeAvailableList.get(i);
                quantityTemp = numberOfGuests / roomTypeHouseholdAvailableWithFullInfoDto.getCapacity();
                check = quantityTemp - Math.toIntExact(roomTypeHouseholdAvailableWithFullInfoDto.getQuantity());
                capacityTemp += Math.toIntExact(roomTypeHouseholdAvailableWithFullInfoDto.getQuantity()) * roomTypeHouseholdAvailableWithFullInfoDto.getCapacity();

                if(dto == null) {
                    remainingRoomTypeCapacity = capacityAvailable - capacityTemp;
                } else {
                    remainingRoomTypeCapacity = currentDto.getCapacityAvailable() - capacityTemp;
                }
                if (check <= 0) {
                    numberOfGuests -= quantityTemp * roomTypeHouseholdAvailableWithFullInfoDto.getCapacity();
                    if (remainingRoomTypeCapacity >= numberOfGuests) {
                        quantityOfRoomType = quantityTemp;
                    } else {
                        quantityOfRoomType = 1;
                        numberOfGuests -= roomTypeHouseholdAvailableWithFullInfoDto.getCapacity();
                    }
                } else {
                    quantityOfRoomType = Math.toIntExact(roomTypeHouseholdAvailableWithFullInfoDto.getQuantity());
                    numberOfGuests -= Math.toIntExact(roomTypeHouseholdAvailableWithFullInfoDto.getQuantity()) * roomTypeHouseholdAvailableWithFullInfoDto.getCapacity();
                }
                if(quantityOfRoomType > 0) {
                    BookingDetailRecommendDto bookingDetailRecommendDto = bookingDetailRecommendResponseMapper.mapper(roomTypeHouseholdAvailableWithFullInfoDto);
                    bookingDetailRecommendDto.setHomestayId(currentDto.getHomestayId());
                    bookingDetailRecommendDto.setHomestayCode(currentDto.getHomestayCode());
                    bookingDetailRecommendDto.setQuantity(quantityOfRoomType);
                    bookingDetailRecommendDtoList.add(bookingDetailRecommendDto);
                }
                if (numberOfGuests <= 0) {
                    return bookingDetailRecommendDtoList;
                }
            }
            if (dto != null) {
                break;
            }
        }
        return bookingDetailRecommendDtoList;
    }

    @Override
    public List<HouseholdDto> findAllHousehold() {
        List<HouseholdDto> householdDtoList = new ArrayList<>();
        List<Household> householdList = householdRepository.findAllActive();
        householdList.forEach(household -> householdDtoList.add(setHouseholdInfo(household)));
        return householdDtoList;
    }

    @Override
    public HouseholdForBookingResponseDto searchHousehold(Long householdId, LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfGuests) {
        Household household = householdRepository.findByIdAndStatusActive(householdId)
                .orElseThrow(() -> new ResourceNotFoundException("household.not.found"));
        HouseholdForBookingResponseDto householdForBookingResponseDto = setHouseholdForBookingInfo(household);
        householdForBookingResponseDto.setNumberOfGuests(numberOfGuests);
        householdForBookingResponseDto.setNumberOfNight(Math.toIntExact(ChronoUnit.DAYS.between(checkInDate, checkOutDate)));

        List<HomestayAndTypeRoomAvailableDto> roomAvailableList = roomAvailableList(householdId, checkInDate, checkOutDate);
        householdForBookingResponseDto.setHomestayAndTypeRoomAvailableList(roomAvailableList);

        List<BookingDetailRecommendDto> bookingDetailRecommendList = bookingDetailRecommendList(roomAvailableList, numberOfGuests);
        householdForBookingResponseDto.setBookingDetailRecommendList(bookingDetailRecommendList);
        householdForBookingResponseDto.setIsSuitable(!bookingDetailRecommendList.isEmpty());
        return householdForBookingResponseDto;
    }

    @Override
    public List<HouseholdForBookingResponseDto> searchHouseholdList(LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfGuests) {
        List<HouseholdForBookingResponseDto> householdForBookingResponseDtoList = new ArrayList<>();
        List<Household> householdList = householdRepository.findAllActive();
        if (householdList == null) {
            return Collections.emptyList();
        }
        householdList.forEach(household -> {
            HouseholdForBookingResponseDto householdForBookingResponseDto = searchHousehold(household.getId(), checkInDate, checkOutDate, numberOfGuests);
            if (Boolean.TRUE.equals(householdForBookingResponseDto.getIsSuitable())) {
                householdForBookingResponseDtoList.add(householdForBookingResponseDto);
            }
        });
        return householdForBookingResponseDtoList;
    }

    @Override
    public List<ServiceResponse> findAllService() {
        return serviceRepository.getAllService();
    }

    @Override
    public boolean checkChooseAvailableRoomType(List<BookingDetailCreateCustomerRequestDto> bookingDetailList,
                                                Long householdId, LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfGuest) {
        List<HomestayAndTypeRoomAvailableDto> roomAvailableList = roomAvailableList(householdId, checkInDate, checkOutDate);
        Map<Long, List<RoomTypeHouseholdAvailableWithFullInfoDto>> roomTypeAvailableMap = roomAvailableList.stream()
                .collect(Collectors.toMap(HomestayAndTypeRoomAvailableDto::getHomestayId, HomestayAndTypeRoomAvailableDto::getRoomTypeAvailableList));

        List<RoomTypeHouseholdAvailableWithFullInfoDto> roomTypeAvailableList;
        for (BookingDetailCreateCustomerRequestDto bookingDetail : bookingDetailList) {
            roomTypeAvailableList = roomTypeAvailableMap.get(bookingDetail.getHomestayId());
            Optional<RoomTypeHouseholdAvailableWithFullInfoDto> optionalRoomType = roomTypeAvailableList.stream()
                    .filter(roomType -> roomType.getRoomTypeHouseholdId().equals(bookingDetail.getHouseholdRoomTypeId()))
                    .findFirst();

            if (optionalRoomType.isEmpty() || optionalRoomType.get().getQuantity() < 1) {
                return false;
            }
            optionalRoomType.get().setQuantity(optionalRoomType.get().getQuantity() - 1);
            roomTypeAvailableList.replaceAll(roomType -> roomType.getRoomTypeHouseholdId().equals(bookingDetail.getHouseholdRoomTypeId()) ? optionalRoomType.get() : roomType);
            roomTypeAvailableMap.put(bookingDetail.getHomestayId(), roomTypeAvailableList);
        }
        return true;
    }


    @Override
    public String bookingRoom(BookingCreateCustomerRequestDto bookingCreateCustomerRequestDto) throws MessagingException {
        //Check user
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        if (currentUserLoginEmailOptional.isEmpty()) {
            throw new ResourceNotFoundException("user.not.found");
        }
        String userEmail = currentUserLoginEmailOptional.get();
        Customer customer = customerRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found"));

        // Check customer name
        if (bookingCreateCustomerRequestDto.getCustomerName() == null || bookingCreateCustomerRequestDto.getCustomerName().isEmpty()) {
            bookingCreateCustomerRequestDto.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
        }
        //Check that customers intentionally break the system
        if (checkCustomerBreakSystem(customer)) {
            throw new ResourceUnauthorizedException("customer.break.system");
        }

        //Create booking
        Booking booking = bookingCreateCustomerRequestMapper.mapToBooking(bookingCreateCustomerRequestDto);
        booking.setBookingCode(generateBookingCode(customer.getId(), booking.getHousehold().getId()));
        booking.setTotalRoom(bookingCreateCustomerRequestDto.getBookingDetailList().size());
        booking.setStatus(BookingStatus.PENDING);
        booking.setCustomer(customer);

        //Create booking detail
        List<BookingDetailCreateCustomerRequestDto> bookingDetailCreateCustomerRequestDtoList = bookingCreateCustomerRequestDto.getBookingDetailList();
        if (bookingDetailCreateCustomerRequestDtoList == null || bookingDetailCreateCustomerRequestDtoList.isEmpty()) {
            throw new ResourceBadRequestException("booking.detail.not.found");
        }
        List<BookingDetail> bookingDetailList = new ArrayList<>();
        List<Room> roomList = bookingDetailRepository.findAllRoomAvailable(bookingCreateCustomerRequestDto.getHouseholdId(), bookingCreateCustomerRequestDto.getCheckInDate(), bookingCreateCustomerRequestDto.getCheckOutDate());
        List<DormSlot> dormSlotList = bookingDetailRepository.findAllDormSlotAvailable(bookingCreateCustomerRequestDto.getHouseholdId(), bookingCreateCustomerRequestDto.getCheckInDate(), bookingCreateCustomerRequestDto.getCheckOutDate());
        int index;
        for (BookingDetailCreateCustomerRequestDto detailCreateDto : bookingDetailCreateCustomerRequestDtoList) {
            BookingDetail bookingDetail = new BookingDetail();
            bookingDetail.setBooking(booking);
            bookingDetail.setHomestay(homestayRepository.findById(detailCreateDto.getHomestayId()).orElseThrow(() -> new ResourceNotFoundException("homestay.not.found")));
            bookingDetail.setHouseholdRoomType(householdRoomTypeRepository.findById(detailCreateDto.getHouseholdRoomTypeId()).orElseThrow(() -> new ResourceNotFoundException("household.room.type.not.found")));
            bookingDetail.setCheckInCustomerName(detailCreateDto.getCustomerCheckInName());
            BigDecimal price = householdRoomTypeRepository.getPriceByHouseholdRoomTypeId(detailCreateDto.getHouseholdRoomTypeId());
            bookingDetail.setPrice(price);
            bookingDetail.setSubTotal(price.multiply(BigDecimal.valueOf(booking.getTotalNight())));
            bookingDetail.setStatus(BookingDetailStatus.PENDING);

            //Choose room or slot dormitory
            index = -1;
            if(!householdRoomTypeRepository.getIsDormitoryByHouseholdRoomTypeId(detailCreateDto.getHouseholdRoomTypeId())) {
                for (int i = 0; i < roomList.size(); i++) {
                    if (roomList.get(i).getHouseholdRoomType().getId().equals(detailCreateDto.getHouseholdRoomTypeId())) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    bookingDetail.setRoom(roomList.get(index));
                    roomList.remove(index);
                } else {
                    throw new ResourceBadRequestException("error.booking");
                }
            } else {
                if (dormSlotList.isEmpty()) {
                    throw new ResourceBadRequestException("error.booking");
                } else {
                    bookingDetail.setDormSlot(dormSlotList.get(0));
                    bookingDetail.setRoom(dormSlotList.get(0).getRoom());
                    dormSlotList.remove(0);
                }
            }
            bookingDetailList.add(bookingDetail);
        }

        //Create Payment
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setType(PaymentType.SYSTEM);
        switch (bookingCreateCustomerRequestDto.getPaymentGateway()) {
            case "VN_PAY":
                payment.setGateway(PaymentGateway.VN_PAY);
                break;
            default:
                throw new ResourceBadRequestException("payment.gateway.not.found");
        }
        payment.setStatus(PaymentStatus.UNPAID);
        booking.setPayment(payment);
        booking.setBookingDetails(bookingDetailList);
        try {
            bookingRepository.save(booking);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
        return booking.getBookingCode();
    }

    private boolean checkCustomerBreakSystem(Customer customer) throws MessagingException {
        LocalDateTime now = LocalDateTime.now();
        int[] timeFrames = {1, 24, 7 * 24}; // Hours
        int[] limits = {5, 6, 10}; // Booking limits for each time frame
        String[] timeFrameNames = {"1 giờ", "24 giờ", "1 tuần"};

        Account account = customer.getAccount();
        int[] counts = new int[3];
        int count;

        try {
            for (int i = 0; i < 3; i++) {
                counts[i] = bookingRepository.countBookingInAnHour(customer.getId(), now.minusHours(timeFrames[i]));
                if (counts[i] >= limits[i]) {
                    account.setStatus(AccountStatus.INACTIVE);
                    accountRepository.save(account);
                    String message = counts[i] + " lần trong vòng " + timeFrameNames[i];
                    sendEmailNoticeBanUser(customer, message);
                    return true;
                }
            }
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }

        for (int i = 0; i < 3; i++) {
            if (counts[i] == limits[i] - 1) {
                String message = counts[i] + " lần trong vòng " + timeFrameNames[i];
                sendEmailNotice(customer, message);
                return false;
            }
        }

        return false;
    }

    private void sendEmailNotice(Customer customer, String notice) throws MessagingException {
        //Customer info
        Account account = customer.getAccount();
        String email = account.getEmail();

        //Phone of village
        String phoneOfVillage = "";
        String tel = "tel:";
        List<String> phone = contactUsRepository.getPhoneOfVillage().orElse(new ArrayList<>());
        if (!phone.isEmpty()) {
            phoneOfVillage = "+84 " + phone.get(0).substring(1);
            tel += "+84" + phone.get(0).substring(1);
        }

        //Email info
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("[Làng H'Mông Pả Vi] Bạn gặp khó khăn khi thanh toán đặt phòng?");
        Context context = new Context();
        context.setVariable("customerName", customer.getFirstName() + " " + customer.getLastName());
        context.setVariable("notice", notice);
        context.setVariable("phoneOfVillage", phoneOfVillage);
        context.setVariable("tel", tel);
        String html = templateEngine.process("warning", context);
        helper.setText(html, true);
        emailSender.send(message);
    }

    private void sendEmailNoticeBanUser(Customer customer, String notice) throws MessagingException {
        //Customer info
        Account account = customer.getAccount();
        String email = account.getEmail();

        //Phone of village
        String phoneOfVillage = "";
        String tel = "tel:";
        List<String> phone = contactUsRepository.getPhoneOfVillage().orElse(new ArrayList<>());
        if (!phone.isEmpty()) {
            phoneOfVillage = "+84 " + phone.get(0).substring(1);
            tel += "+84" + phone.get(0).substring(1);
        }

        //Email info
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("[Làng H'Mông Pả Vi] Tài khoản của bạn đã bị vô hiệu hóa");
        Context context = new Context();
        context.setVariable("customerName", customer.getFirstName() + " " + customer.getLastName());
        context.setVariable("notice", notice);
        context.setVariable("phoneOfVillage", phoneOfVillage);
        context.setVariable("tel", tel);
        String html = templateEngine.process("warning-ban", context);
        helper.setText(html, true);
        emailSender.send(message);
    }

    @Override
    public void updateBooking(BookingInfoForUpdateDto bookingInfoForUpdateDto) {
        Booking booking = bookingRepository.findBookingByBookingCode(bookingInfoForUpdateDto.getBookingCode()).orElseThrow(() -> new ResourceNotFoundException("booking.not.found"));
        try {
            booking.setCheckInName(bookingInfoForUpdateDto.getCustomerName());
            booking.setCheckInPhoneNumber(bookingInfoForUpdateDto.getCustomerPhone());
            bookingRepository.save(booking);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public BookingCancelCustomerResponseDto findBookingCancelFormByCustomer(String bookingCode) {
        String getCurrentUserEmail = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new ResourceNotFoundException("user.not.found"));
        Booking booking = bookingRepository.findBookingByBookingCode(bookingCode).orElseThrow(() -> new ResourceNotFoundException("booking.not.found"));

        if (!booking.getCustomer().getAccount().getEmail().equals(getCurrentUserEmail)) {
            throw new ResourceForbiddenException("booking.denied.cancel");
        }

        int cancellationPeriod = booking.getHousehold().getCancellationPeriod();
        LocalDate cancellationDate = LocalDate.now();
        LocalDate cancellationDeadline = booking.getCheckInDate().minusDays(cancellationPeriod);

        BigDecimal refundAmount;
        String status;
        // Nếu hủy sau thời hạn hủy thì không hoàn tiền
        if (cancellationDate.isAfter(cancellationDeadline)) {
            refundAmount = new BigDecimal("0");
            status = "NOT_REFUND";
        } else {
            // Nếu hủy trước thời hạn hủy thì hoàn tiền 100%
            refundAmount = booking.getTotalPrice();
            status = "REFUND";
        }

        List<CancellationReason> cancellationReasons = cancellationReasonService.findAll();

        BookingCancelCustomerResponseDto bookingCancelCustomerResponseDto = new BookingCancelCustomerResponseDto();
        bookingCancelCustomerResponseDto.setBookingCode(bookingCode);
        bookingCancelCustomerResponseDto.setRefundAmount(refundAmount);
        bookingCancelCustomerResponseDto.setCancellationReasons(cancellationReasons);
        bookingCancelCustomerResponseDto.setStatus(status);

        return bookingCancelCustomerResponseDto;
    }

    @Override
    public void cancelBooking(BookingCancelCustomerRequestDto bookingCancelCustomerRequestDto) {
        Booking booking = bookingRepository.findBookingByBookingCode(bookingCancelCustomerRequestDto.getBookingCode()).orElseThrow(() -> new ResourceNotFoundException("booking.not.found"));
        cancelBooking(booking);
        try {
            List<BookingDetail> bookingDetailList = booking.getBookingDetails();
            bookingDetailService.cancelBookingDetail(bookingDetailList);

            CancellationHistory cancellationHistory = new CancellationHistory();
            cancellationHistory.setBooking(booking);
            cancellationHistory.setCancellationReason(bookingCancelCustomerRequestDto.getCancelReason());
            cancellationHistory.setRefundAmount(bookingCancelCustomerRequestDto.getRefundAmount());
            cancellationHistory.setCustomer(booking.getCustomer());
            cancellationHistory.setCancellationDate(LocalDateTime.now());
            if (bookingCancelCustomerRequestDto.getStatus().equals("NOT_REFUND")) {
                cancellationHistory.setRefundStatus(RefundStatus.NOT_REFUNDED);
            } else {
                cancellationHistory.setRefundStatus(RefundStatus.PENDING);

                CustomerBankInformation customerBankInformation = customerBankInformationService.findByCustomerId(booking.getCustomer().getId());
                if (customerBankInformation == null) {
                    customerBankInformation = new CustomerBankInformation();
                    customerBankInformation.setCustomer(booking.getCustomer());
                }
                customerBankInformation.setBankName(bookingCancelCustomerRequestDto.getBankName());
                customerBankInformation.setAccountNumber(bookingCancelCustomerRequestDto.getAccountNumber());
                customerBankInformation.setAccountHolder(bookingCancelCustomerRequestDto.getAccountOwnerName());
                customerBankInformationService.saveCustomerBankInformation(customerBankInformation);
            }
            cancellationHistoryService.saveCancellationHistory(cancellationHistory);

            //send email
            if (bookingCancelCustomerRequestDto.getStatus().equals("NOT_REFUND")) {
                sendEmailBookingCancelNotRefund(booking.getBookingCode());
            } else {
                sendEmailBookingCancelRefund(bookingCancelCustomerRequestDto);
            }

            //send notification
            NotificationBookingCancelRequest notificationBookingCancelRequest = new NotificationBookingCancelRequest();
            notificationBookingCancelRequest.setTitle("Thông báo hủy đặt phòng");
            notificationBookingCancelRequest.setBookingCode(booking.getBookingCode());
            notificationBookingCancelRequest.setCustomerName(booking.getCheckInName());
            notificationBookingCancelRequest.setRefundAmount(bookingCancelCustomerRequestDto.getStatus().equals("NOT_REFUND") ? new BigDecimal("0") : bookingCancelCustomerRequestDto.getRefundAmount());
            notificationBookingCancelRequest.setDeadlineRefundDate(LocalDate.now().plusDays(2));
            notificationService.pushBookingCancelNotification(booking.getHousehold().getManager().getId(), notificationBookingCancelRequest);

        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    private void sendEmailBookingCancelRefund(BookingCancelCustomerRequestDto dto) throws MessagingException {
        Booking booking = bookingRepository.findBookingByBookingCode(dto.getBookingCode()).orElseThrow(() -> new ResourceNotFoundException("booking.not.found"));

        //Customer info
        Customer customer = booking.getCustomer();
        Account account = customer.getAccount();
        String email = account.getEmail();

        //Household info
        Household household = booking.getHousehold();
        String houseName = household.getHouseholdName();
        String phoneNumber = household.getPhoneNumberFirst();

        //Booking info
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, 'ngày' dd 'tháng' MM 'năm' yyyy", new Locale("vi"));
        String checkInDateBooking = booking.getCheckInDate().format(formatter);
        String checkOutDateBooking = booking.getCheckOutDate().format(formatter);
        DecimalFormat formatterPrice = new DecimalFormat("#,###");
        String totalPriceBooking = formatterPrice.format(booking.getTotalPrice()) + "VNĐ";
        String refundAmount = formatterPrice.format(dto.getRefundAmount()) + "VNĐ";
        booking.setCreatedDate(booking.getCreatedDate().plusHours(DateTimeConstant.HOURS_DEPLOY));

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("[Làng H'Mông Pả Vi] Hủy phòng của bạn ở " + houseName + " đã được xác nhận");
        Context context = new Context();
        context.setVariable("bookingCode", dto.getBookingCode());
        context.setVariable("customerName", customer.getFirstName() + " " + customer.getLastName());
        context.setVariable("customerBooking", booking.getCheckInName());
        context.setVariable("customerPhone", booking.getCheckInPhoneNumber());
        context.setVariable("householdName", houseName);
        context.setVariable("phoneNumber", phoneNumber);
        context.setVariable("checkInDate", checkInDateBooking);
        context.setVariable("checkOutDate", checkOutDateBooking);
        context.setVariable("totalPrice", totalPriceBooking);
        context.setVariable("refundAmount", refundAmount);
        context.setVariable("bankName", dto.getBankName());
        context.setVariable("accountNumber", dto.getAccountNumber());
        context.setVariable("accountOwnerName", dto.getAccountOwnerName());
        String html = templateEngine.process("cancel-booking-refund-confirm", context);
        helper.setText(html, true);
        emailSender.send(message);
    }

    private void sendEmailBookingCancelNotRefund(String bookingCode) throws MessagingException {
        Booking booking = bookingRepository.findBookingByBookingCode(bookingCode).orElseThrow(() -> new ResourceNotFoundException("booking.not.found"));

        //Customer info
        Customer customer = booking.getCustomer();
        Account account = customer.getAccount();
        String email = account.getEmail();

        //Household info
        Household household = booking.getHousehold();
        String houseName = household.getHouseholdName();
        String phoneNumber = household.getPhoneNumberFirst();

        //Booking info
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, 'ngày' dd 'tháng' MM 'năm' yyyy", new Locale("vi"));
        String checkInDateBooking = booking.getCheckInDate().format(formatter);
        String checkOutDateBooking = booking.getCheckOutDate().format(formatter);
        DecimalFormat formatterPrice = new DecimalFormat("#,###");
        String totalPriceBooking = formatterPrice.format(booking.getTotalPrice()) + "VNĐ";
        booking.setCreatedDate(booking.getCreatedDate().plusHours(DateTimeConstant.HOURS_DEPLOY));


        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("[Làng H'Mông Pả Vi] Hủy phòng của bạn ở " + houseName + " đã được xác nhận");
        Context context = new Context();
        context.setVariable("bookingCode", bookingCode);
        context.setVariable("customerName", customer.getFirstName() + " " + customer.getLastName());
        context.setVariable("customerBooking", booking.getCheckInName());
        context.setVariable("customerPhone", booking.getCheckInPhoneNumber());
        context.setVariable("householdName", houseName);
        context.setVariable("phoneNumber", phoneNumber);
        context.setVariable("checkInDate", checkInDateBooking);
        context.setVariable("checkOutDate", checkOutDateBooking);
        context.setVariable("totalPrice", totalPriceBooking);
        String html = templateEngine.process("cancel-booking-not-refund-confirm", context);
        helper.setText(html, true);
        emailSender.send(message);
    }

    @Override
    public String findHouseNameByBookingCode(String bookingCode) {
        return bookingRepository.findHouseNameByBookingCode(bookingCode);
    }

    @Override
    public void sendEmailBookingSuccess(String bookingCode) throws MessagingException {
        Booking booking = bookingRepository.findBookingByBookingCode(bookingCode).orElseThrow(() -> new ResourceNotFoundException("booking.not.found"));
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
        booking.setCreatedDate(booking.getCreatedDate().plusHours(DateTimeConstant.HOURS_DEPLOY));

        //Payment info
        Payment payment = booking.getPayment();
        String paymentDate = payment.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("[Làng H'Mông Pả Vi] Đặt phòng của bạn ở " + houseName + " đã được xác nhận");
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
        Booking booking = bookingRepository.findBookingByBookingCode(bookingCode).orElseThrow(() -> new ResourceNotFoundException("booking.not.found"));
        try {
            booking.setStatus(status);
            bookingRepository.save(booking);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    private void cancelBooking(Booking booking) {
        try {
            if (booking.getStatus().equals(BookingStatus.CANCELLED)) {
                throw new ResourceBadRequestException("booking.cancelled");
            } else {
                booking.setStatus(BookingStatus.CANCELLED);
            }
            bookingRepository.save(booking);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public String generateBookingCode(Long cusId, Long householdId) {
        String bookingCode;
        long duration;
        do {
            duration = Duration.between(Instant.parse("2023-08-05T00:00:00Z"), Instant.now()).getSeconds() + 1;
            bookingCode = "PAVI" + householdId + cusId + duration;
        } while (bookingRepository.existsByBookingCode(bookingCode).isPresent());
        return bookingCode;
    }

    @Override
    public BigDecimal findMaxPrice(List<HouseholdForBookingResponseDto> householdListResponseDto, long nightStay){
        AtomicReference<AtomicReference<BigDecimal>> maxPrice = new AtomicReference<>(new AtomicReference<>(BigDecimal.ZERO));
        householdListResponseDto.forEach(household -> {
            AtomicReference<BigDecimal> price = new AtomicReference<>(BigDecimal.ZERO);
            List<BookingDetailRecommendDto> bookingDetailRecommendDtoList = household.getBookingDetailRecommendList();
            for (BookingDetailRecommendDto bookingDetailRecommendDto : bookingDetailRecommendDtoList){
                price.set(price.get().add(bookingDetailRecommendDto.getPrice().multiply(BigDecimal.valueOf(bookingDetailRecommendDto.getQuantity())).multiply(BigDecimal.valueOf(nightStay))));
            }
            if (price.get().compareTo(maxPrice.get().get()) > 0) {
                maxPrice.set(price);
            }
        });
        return maxPrice.get().get();
    }
}

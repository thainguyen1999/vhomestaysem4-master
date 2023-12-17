package com.example.vhomestay.service.impl;

import com.example.vhomestay.model.dto.response.report.admin.ReportDetailForAdminResponse;
import com.example.vhomestay.model.dto.response.report.admin.ReportDetailListResponseForAdmin;
import com.example.vhomestay.model.dto.response.report.admin.ReportHouseholdPayment;
import com.example.vhomestay.model.dto.response.report.manager.BookingReportDetailForManagerResponse;
import com.example.vhomestay.model.dto.response.report.manager.RoomTypeReportDetailForManagerResponse;
import com.example.vhomestay.model.entity.*;
import com.example.vhomestay.repository.*;
import com.example.vhomestay.security.SecurityUtil;
import com.example.vhomestay.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final HouseholdRepository householdRepository;
    private final HomestayRepository homestayRepository;
    private final HouseholdRoomTypeRepository householdRoomTypeRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    private ReportDetailForAdminResponse getReportDetailForAdmin(Long householdId, LocalDate checkInDate, LocalDate checkOutDate) {

        Integer totalHomestay = 0;
        Integer totalCapacity = 0;
        Integer totalCustomer = 0;
        Integer totalCustomerByDay = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        ReportDetailForAdminResponse reportDetailForAdminResponse = new ReportDetailForAdminResponse();

        Household household = householdRepository.findById(householdId).get();

        List<Homestay> homestayList = homestayRepository.findByHouseholdId(householdId);
        totalHomestay = homestayList.size();

        List<String> homestayName = new ArrayList<>();

        for (Homestay homestay : homestayList) {
            homestayName.add(homestay.getHomestayCode());
            List<Room> roomList = roomRepository.findAllByHomestayId(homestay.getId());
            if (roomList != null) {
                for (Room room : roomList) {
                    if (!room.getHouseholdRoomType().getRoomType().getIsDorm()){
                        totalCapacity += room.getHouseholdRoomType().getCapacity();
                    } else {
                        totalCapacity += roomRepository.countDormSlotByRoomId(room.getId());
                    }
                }
            }
        }

        List<Booking> bookingList = bookingRepository.getAllBookingByHouseholdId(householdId, checkInDate, checkOutDate);
        for (Booking booking : bookingList) {
            if (booking.getTotalGuest()!= null){
                totalCustomer += booking.getTotalGuest();
                Period period = Period.between(booking.getCheckInDate(), booking.getCheckOutDate());
                Integer days = (int) period.getDays();
                totalCustomerByDay += booking.getTotalGuest() * (days);
            }
            totalRevenue = totalRevenue.add(booking.getTotalPrice());
        }

        reportDetailForAdminResponse.setHouseholdId(householdId);
        reportDetailForAdminResponse.setHouseholdName(household.getHouseholdName());
        if (household.getManager()==null){
            reportDetailForAdminResponse.setManagerName("Không có nguời quản lí");
        } else {
            if (household.getManager().getFirstName()!=null && household.getManager().getLastName()!=null){
                reportDetailForAdminResponse.setManagerName(household.getManager().getFirstName() + " " + household.getManager().getLastName());
            } else if (household.getManager().getFirstName()!=null && household.getManager().getLastName()==null){
                reportDetailForAdminResponse.setManagerName(household.getManager().getFirstName());
            } else if (household.getManager().getFirstName()==null && household.getManager().getLastName()!=null){
                reportDetailForAdminResponse.setManagerName(household.getManager().getLastName());
            } else {
                reportDetailForAdminResponse.setManagerName("Không có thông tin");
            }
        }
        reportDetailForAdminResponse.setHomeStayName(homestayName);
        reportDetailForAdminResponse.setTotalHomestay(totalHomestay);
        reportDetailForAdminResponse.setTotalCapacity(totalCapacity);
        reportDetailForAdminResponse.setTotalCustomer(totalCustomer);
        reportDetailForAdminResponse.setTotalCustomerByDay(totalCustomerByDay);
        reportDetailForAdminResponse.setTotalRevenue(totalRevenue);
        return reportDetailForAdminResponse;
    }
    @Override
    public ReportDetailListResponseForAdmin getReportForAdmin(LocalDate checkInDate, LocalDate checkOutDate) {
        List<Household> householdList = householdRepository.getAllHousehold();
        List<ReportDetailForAdminResponse> reportDetailForAdminResponseList = new ArrayList<>();
        for (Household household : householdList) {
            reportDetailForAdminResponseList.add(getReportDetailForAdmin(household.getId(), checkInDate, checkOutDate));
        }
        ReportDetailListResponseForAdmin reportDetailListResponseForAdmin = new ReportDetailListResponseForAdmin();
        reportDetailListResponseForAdmin.setReportDetailListForAdmin(reportDetailForAdminResponseList);
        return reportDetailListResponseForAdmin;
    }

    @Override
    public List<ReportHouseholdPayment> getReportHouseholdPayment(LocalDate checkInDate, LocalDate checkOutDate) {
        List<ReportHouseholdPayment> reportHouseholdPaymentList = paymentRepository.getReportHouseholdPayment(checkInDate.atStartOfDay(), checkOutDate.atStartOfDay());
        return reportHouseholdPaymentList;
    }

    @Override
    public List<BookingReportDetailForManagerResponse> getBookingReportListForManager(LocalDate checkInDate, LocalDate checkOutDate){

        List<BookingReportDetailForManagerResponse> bookingReportDetailListForManagerResponse = new ArrayList<>();

        String managerEmail = SecurityUtil.getCurrentUserLogin().get();
        List<Booking> bookingList = bookingRepository.getAllBookingByManagerEmail(managerEmail, checkInDate, checkOutDate);
        if (bookingList.isEmpty()){
            return bookingReportDetailListForManagerResponse;
        }
        for (Booking booking : bookingList){
            BookingReportDetailForManagerResponse bookingReportDetailForManagerResponse = new BookingReportDetailForManagerResponse();
            bookingReportDetailForManagerResponse.setBookingCode(booking.getBookingCode());
            if (booking.getCustomer()==null){
                bookingReportDetailForManagerResponse.setCustomerName("Không có thông tin");
            } else {
                bookingReportDetailForManagerResponse.setCustomerName(booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName());
            }
            bookingReportDetailForManagerResponse.setCheckInDate(booking.getCreatedDate());
            long daysBetween = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
            bookingReportDetailForManagerResponse.setBookedNight(daysBetween);
            bookingReportDetailForManagerResponse.setTotalCustomer(booking.getTotalGuest());
            bookingReportDetailForManagerResponse.setTotalRevenue(booking.getTotalPrice());
            if (booking.getPayment() != null){
                bookingReportDetailForManagerResponse.setPaymentDate(booking.getPayment().getPaymentDate());
                bookingReportDetailForManagerResponse.setGateway(booking.getPayment().getGateway());
                bookingReportDetailForManagerResponse.setPaymentMethod(booking.getPayment().getType());
            } else {
                bookingReportDetailForManagerResponse.setPaymentDate(null);
                bookingReportDetailForManagerResponse.setGateway(null);
                bookingReportDetailForManagerResponse.setPaymentMethod(null);
            }
            if (booking.getCancellationHistory() != null) {
                bookingReportDetailForManagerResponse.setBookingStatus("CANCELED");
                if (booking.getCancellationHistory().getRefundStatus().equals("REFUNDED")){
                    String note = "Đã hủy và hoàn tiền vào ngày " + booking.getCancellationHistory().getRefundDate();
                    bookingReportDetailForManagerResponse.setNote(note);
                } else {
                    bookingReportDetailForManagerResponse.setRefundDate(null);
                    String note = "Đã hủy nhưng chưa hoàn tiền";
                    bookingReportDetailForManagerResponse.setNote(note);
                }
            } else {
                bookingReportDetailForManagerResponse.setBookingStatus("BOOKED");
                bookingReportDetailForManagerResponse.setRefundDate(null);
                bookingReportDetailForManagerResponse.setNote(null);
            }
            bookingReportDetailListForManagerResponse.add(bookingReportDetailForManagerResponse);
        }
        return bookingReportDetailListForManagerResponse;
    }

    private List<RoomTypeReportDetailForManagerResponse> getRoomTypeDetailReport(String homestayCode, LocalDate checkInDate, LocalDate checkOutDate) {

        List<RoomTypeReportDetailForManagerResponse> roomTypeReportDetailForManagerResponseList = new ArrayList<>();

        List<HouseholdRoomType> householdRoomTypeList = householdRoomTypeRepository.getAllHouseholdRoomTypeByHomestayCode(homestayCode);
        for (HouseholdRoomType householdRoomType : householdRoomTypeList){
            RoomTypeReportDetailForManagerResponse roomTypeReportDetailForManagerResponse = new RoomTypeReportDetailForManagerResponse(0, 0, 0, 0, BigDecimal.ZERO);
            List<Room> roomList = roomRepository.getAllRoomByHomestayCodeAndHouseholdRoomType(homestayCode, householdRoomType.getId());
            List<Booking> bookingList = bookingRepository.getAllBookingByHomestayCodeAndHouseholdRoomType(homestayCode, householdRoomType.getId(), checkInDate, checkOutDate);
            roomTypeReportDetailForManagerResponse.setHomestayCode(homestayCode);
            roomTypeReportDetailForManagerResponse.setRoomTypeName(householdRoomType.getRoomType().getRoomTypeName());
            for (Room room : roomList){
                if (!room.getHouseholdRoomType().getRoomType().getIsDorm()){
                    roomTypeReportDetailForManagerResponse.setTotalRoom(roomTypeReportDetailForManagerResponse.getTotalRoom() + 1);
                } else {
                    roomTypeReportDetailForManagerResponse.setTotalRoom(roomTypeReportDetailForManagerResponse.getTotalRoom() + 1);
                    roomTypeReportDetailForManagerResponse.setTotalDormSlot(roomTypeReportDetailForManagerResponse.getTotalDormSlot() + roomRepository.countDormSlotByRoomId(room.getId()));
                }
            }
            for (Booking booking : bookingList){
                if (booking.getTotalGuest() == null){
                    booking.setTotalGuest(0);
                }
                roomTypeReportDetailForManagerResponse.setTotalCustomer(roomTypeReportDetailForManagerResponse.getTotalCustomer() + booking.getTotalGuest());
                roomTypeReportDetailForManagerResponse.setTotalCustomerByDay(roomTypeReportDetailForManagerResponse.getTotalCustomerByDay() + (booking.getTotalGuest()*(int)(ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate()))));
                roomTypeReportDetailForManagerResponse.setTotalRevenue(roomTypeReportDetailForManagerResponse.getTotalRevenue().add(booking.getTotalPrice()));
            }
            roomTypeReportDetailForManagerResponseList.add(roomTypeReportDetailForManagerResponse);
        }
        return roomTypeReportDetailForManagerResponseList;
    }

    @Override
    public List<RoomTypeReportDetailForManagerResponse> getRoomTypeDetailListReport(LocalDate checkInDate, LocalDate checkOutDate) {
        String managerEmail = SecurityUtil.getCurrentUserLogin().get();
        List<Homestay> homestayList = homestayRepository.findAllHomestayByManagerEmail(managerEmail);
        List<RoomTypeReportDetailForManagerResponse> roomTypeReportDetailForManagerResponseList = new ArrayList<>();
        for (Homestay homestay : homestayList){
            List<RoomTypeReportDetailForManagerResponse> roomTypeReportDetailForManagerResponseListForHomestay = getRoomTypeDetailReport(homestay.getHomestayCode(), checkInDate, checkOutDate);
            for (RoomTypeReportDetailForManagerResponse roomTypeReportDetailForManagerResponse : roomTypeReportDetailForManagerResponseListForHomestay){
                roomTypeReportDetailForManagerResponseList.add(roomTypeReportDetailForManagerResponse);
            }
        }
        return roomTypeReportDetailForManagerResponseList;
    }

    @Override
    public List<BookingReportDetailForManagerResponse> getBookingHaveBeenCancelReportListForManager(LocalDate checkInDate, LocalDate checkOutDate) {
        List<BookingReportDetailForManagerResponse> bookingReportDetailListForManagerResponse = new ArrayList<>();

        String managerEmail = SecurityUtil.getCurrentUserLogin().get();
        List<Booking> bookingList = bookingRepository.getAllCancelBookingByManagerEmail(managerEmail, checkInDate, checkOutDate);
        for (Booking booking : bookingList){
            BookingReportDetailForManagerResponse bookingReportDetailForManagerResponse = new BookingReportDetailForManagerResponse();
            bookingReportDetailForManagerResponse.setBookingCode(booking.getBookingCode());
            if (booking.getCustomer()==null){
                bookingReportDetailForManagerResponse.setCustomerName("Không có thông tin");
            } else {
                bookingReportDetailForManagerResponse.setCustomerName(booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName());
            }
            bookingReportDetailForManagerResponse.setCheckInDate(booking.getLastModifiedDate());
            long daysBetween = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
            bookingReportDetailForManagerResponse.setBookedNight(daysBetween);
            bookingReportDetailForManagerResponse.setTotalCustomer(booking.getTotalGuest());
            bookingReportDetailForManagerResponse.setTotalRevenue(booking.getTotalPrice());
            bookingReportDetailForManagerResponse.setPaymentDate(booking.getPayment().getPaymentDate());
            bookingReportDetailForManagerResponse.setGateway(booking.getPayment().getGateway());
            bookingReportDetailForManagerResponse.setPaymentMethod(booking.getPayment().getType());
            if (booking.getCancellationHistory() != null) {
                bookingReportDetailForManagerResponse.setRefundAmount(booking.getCancellationHistory().getRefundAmount());
                bookingReportDetailForManagerResponse.setCancellationDate(booking.getCancellationHistory().getCancellationDate());
                if (booking.getCancellationHistory().getRefundStatus().equals("REFUNDED")){
                    bookingReportDetailForManagerResponse.setRefundDate(booking.getCancellationHistory().getRefundDate());
                    String note = "Đã hủy và hoàn tiền vào ngày " + booking.getCancellationHistory().getRefundDate();
                    bookingReportDetailForManagerResponse.setNote(note);
                } else {
                    bookingReportDetailForManagerResponse.setRefundDate(null);
                    String note = "Đã hủy nhưng chưa hoàn tiền";
                    bookingReportDetailForManagerResponse.setNote(note);
                }
            }
            bookingReportDetailListForManagerResponse.add(bookingReportDetailForManagerResponse);
        }
        return bookingReportDetailListForManagerResponse;
    }
}

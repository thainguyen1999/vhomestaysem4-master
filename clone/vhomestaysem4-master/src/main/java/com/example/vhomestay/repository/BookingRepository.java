package com.example.vhomestay.repository;

import com.example.vhomestay.model.dto.response.dashboard.manager.BookingCancelDetailForManager;
import com.example.vhomestay.model.dto.response.feedback.AddFeedbackForm;
import com.example.vhomestay.model.dto.response.user.BookingOfCustomerDto;
import com.example.vhomestay.model.entity.Booking;
import com.example.vhomestay.model.entity.DormSlot;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends BaseRepository<Booking, Long>{
    @Query("SELECT b " +
            "FROM Booking b JOIN b.customer c " +
            "JOIN c.account a WHERE a.email = :email ORDER BY b.createdDate DESC")
    List<Booking> findBookingsByCustomerEmail(@Param("email") String email);
    Optional<Booking> findBookingByBookingCode(String bookingCode);
    @Query("SELECT b " +
            "FROM Booking b JOIN b.household h " +
            "JOIN h.manager m JOIN m.account a " +
            "WHERE a.email = :email AND b.status != 'PENDING' AND (:searchValue IS NULL OR b.bookingCode LIKE %:searchValue% " +
            "OR b.checkInName LIKE %:searchValue% " +
            "OR b.checkInPhoneNumber LIKE %:searchValue%) " +
            "AND (:checkInDate IS NULL OR b.checkInDate = :checkInDate) " +
            "AND (:checkOutDate IS NULL OR b.checkOutDate = :checkOutDate) ORDER BY b.createdDate DESC")
    List<Booking> findBookingsByManagerEmail(@Param("email") String managerEmail,
                                             @Param("searchValue") String searchValue,
                                             @Param("checkInDate") LocalDate checkInDate,
                                             @Param("checkOutDate") LocalDate checkOutDate);
    @Query("SELECT new com.example.vhomestay.model.dto.response.feedback.AddFeedbackForm(" +
            "b.bookingCode, hs.homestayCode, b.totalRoom, b.totalGuest,b.checkInDate, b.checkOutDate, b.totalPrice) " +
            "FROM Booking b join b.bookingDetails bdt join bdt.homestay hs WHERE b.bookingCode = :bookingCode")
    Optional<AddFeedbackForm> getBookingDetailByBookingCodeForAddFeedback(String bookingCode);

    @Query("SELECT h.householdName FROM Booking b JOIN b.household h WHERE b.bookingCode = :bookingCode")
    String findHouseNameByBookingCode(String bookingCode);

    @Query("SELECT b FROM Booking b WHERE b.bookingCode = :bookingCode")
    Optional<Booking> existsByBookingCode(String bookingCode);

    @Query("SELECT b FROM Booking b JOIN b.household h JOIN h.homestay ht JOIN h.manager m JOIN m.account a WHERE a.email = :emailManager " +
            "AND b.status = com.example.vhomestay.enums.BookingStatus.BOOKED " +
            "AND b.checkInDate = :checkInDate ")
    List<Booking> findBookingsCheckInByManager(@Param("emailManager") String emailManager,
                                               @Param("checkInDate") LocalDate checkInDate);

    @Query("SELECT b FROM Booking b JOIN b.household h JOIN h.homestay ht JOIN h.manager m JOIN m.account a WHERE a.email = :emailManager " +
            "AND b.status = com.example.vhomestay.enums.BookingStatus.CHECKED_IN " +
            "AND b.checkOutDate = :checkOutDate ")
    List<Booking> findBookingsCheckOutByManager(String emailManager, LocalDate checkOutDate);
    @Query("SELECT ds FROM Booking b JOIN b.bookingDetails bd JOIN bd.dormSlot ds WHERE b.bookingCode = :bookingCode")
    List<DormSlot> findDormSlotsByBookingCode(@Param("bookingCode") String bookingCode);
    @Query("SELECT b FROM Booking b JOIN b.household h WHERE h.id = :householdId AND b.checkInDate >= :checkInDate AND b.checkOutDate <= :checkOutDate")
    List<Booking> getAllBookingByHouseholdId(Long householdId, LocalDate checkInDate, LocalDate checkOutDate);
    @Query("SELECT b FROM Booking b JOIN b.household h join h.manager m join m.account a WHERE a.email = :managerEmail AND b.checkInDate >= :checkInDate AND b.checkOutDate <= :checkOutDate AND b.checkInDate < :checkOutDate")
    List<Booking> getAllBookingByManagerEmail(String managerEmail, LocalDate checkInDate, LocalDate checkOutDate);
    @Query("SELECT b FROM Booking b JOIN b.household h join h.manager m join m.account a WHERE a.email = :managerEmail AND b.checkInDate >= :checkInDate AND b.checkOutDate <= :checkOutDate AND b.status = 'CANCELLED'")
    List<Booking> getAllCancelBookingByManagerEmail(String managerEmail, LocalDate checkInDate, LocalDate checkOutDate);
    @Query("select b from Booking b join b.bookingDetails bds join bds.room r join r.householdRoomType hsrt join r.homestay hs where hs.homestayCode = :homestayCode and hsrt.id = :householdRoomTypeId" +
            " and b.checkInDate >= :checkInDate and b.checkOutDate <= :checkOutDate")
    List<Booking> getAllBookingByHomestayCodeAndHouseholdRoomType(String homestayCode, Long householdRoomTypeId, LocalDate checkInDate, LocalDate checkOutDate);
    @Query("SELECT new com.example.vhomestay.model.dto.response.user.BookingOfCustomerDto(" +
            "b.bookingCode, h.avatar, h.householdName, b.checkInDate, b.checkOutDate, b.totalGuest, b.totalNight, b.totalPrice, b.status) " +
            "FROM Booking b JOIN b.household h JOIN b.customer c JOIN c.account a " +
            "WHERE a.id = :accountId AND b.status != 'PENDING' AND b.status != 'DELETED' ORDER BY b.bookingCode DESC")
    List<BookingOfCustomerDto> getBookingOfCustomer(Long accountId);
    @Query("SELECT SUM(b.totalGuest) FROM Booking b WHERE month(b.checkInDate) = :month AND year(b.checkInDate) = :year AND b.checkInDate < CURRENT_TIMESTAMP() AND (b.status = 'CHECKED_IN' OR b.status = 'CHECKED_OUT')")
    Integer countAllBookingGuestByMonthAndYear(Integer month, Integer year);
    @Query("SELECT count(b.totalGuest) FROM Booking b join b.household h join h.manager m join m.account a " +
            "WHERE b.checkInDate = CURRENT_TIMESTAMP() AND b.status = 'BOOKED' AND a.email = :emailManager")
    Integer countAllCheckInTodayByManager(String emailManager);
    @Query("SELECT count(b.totalGuest) FROM Booking b join b.household h join h.manager m join m.account a " +
            "WHERE b.checkOutDate = CURRENT_TIMESTAMP() AND b.status = 'CHECKED_IN' AND a.email = :emailManager")
    Integer countAllCheckOutTodayByManager(String emailManager);
    @Query("SELECT count(b.totalGuest) FROM Booking b join b.household h join h.manager m join m.account a " +
            "WHERE b.checkInDate = CURRENT_TIMESTAMP() AND (b.status = 'BOOKED' OR b.status = 'CHECKED_IN') AND a.email = :emailManager")
    Integer countAllBookingTodayByManager(String emailManager);
    @Query("SELECT new com.example.vhomestay.model.dto.response.dashboard.manager.BookingCancelDetailForManager(" +
            "b.bookingCode, clh.cancellationDate, c.firstName, c.lastName, clh.refundAmount) FROM Booking b join b.cancellationHistory clh join b.customer c join b.household h join h.manager m join m.account a " +
            "WHERE b.status = 'CANCELLED' AND clh.refundStatus = 'PENDING' AND a.email = :emailManager")
    List<BookingCancelDetailForManager> getBookingCancelDetailForManager(String emailManager);
    @Query("SELECT COUNT(b) FROM Booking b JOIN b.customer c " +
            "WHERE (b.status = 'PENDING' OR b.status = 'DELETED') " +
            "AND b.createdDate >= :dateTime " +
            "AND c.id = :id")
    int countBookingInAnHour(Long id, LocalDateTime dateTime);
}

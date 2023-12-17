package com.example.vhomestay.repository;

import com.example.vhomestay.model.dto.response.booking.manager.BookingSummaryDto;
import com.example.vhomestay.model.dto.response.booking.customer.RoomTypeHouseholdAvailableDto;
import com.example.vhomestay.model.dto.response.bookingdetail.BookingDetailCheckInOutTodayResponseDto;
import com.example.vhomestay.model.entity.BookingDetail;
import com.example.vhomestay.model.entity.DormSlot;
import com.example.vhomestay.model.entity.Room;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingDetailRepository extends BaseRepository<BookingDetail, Long>{
    @Query("SELECT bd FROM BookingDetail bd JOIN bd.booking b WHERE b.bookingCode = :bookingCode")
    List<BookingDetail> findBookingDetailByBookingCode(@Param("bookingCode") String bookingCode);
    @Query("SELECT new com.example.vhomestay.model.dto.response.booking.manager.BookingSummaryDto(db.householdRoomType.id, rt.roomTypeName, COUNT(*), SUM(db.subTotal)) " +
            "FROM BookingDetail db " +
            "INNER JOIN db.householdRoomType hrt " +
            "INNER JOIN hrt.roomType rt " +
            "WHERE db.booking.bookingCode LIKE %:bookingCode% " +
            "GROUP BY db.householdRoomType.id, rt.roomTypeName")
    List<BookingSummaryDto> findBookingSummaryByBookingCode(@Param("bookingCode") String bookingCode);

    @Query("SELECT new com.example.vhomestay.model.dto.response.booking.customer.RoomTypeHouseholdAvailableDto(hrt.id, rt.roomTypeName, hrt.capacity, rt.singleBed, rt.doubleBed, hrt.price, hrt.isChildrenAndBed, rt.isDorm, COUNT(*)) " +
            "FROM Room r " +
            "JOIN r.householdRoomType hrt " +
            "JOIN hrt.roomType rt " +
            "JOIN r.homestay h " +
            "WHERE h.id = :homestayId AND rt.isDorm = false AND h.status = 'ACTIVE' AND r.status = 'ACTIVE' AND r.id NOT IN " +
                    "(SELECT r.id " +
                    "FROM BookingDetail db " +
                    "JOIN db.room r " +
                    "JOIN r.householdRoomType hrt " +
                    "JOIN hrt.roomType rt " +
                    "JOIN r.homestay hs " +
                    "JOIN db.booking b " +
                    "WHERE (db.status = 'BOOKED' OR db.status = 'CHECKED_IN' OR (db.status = 'PENDING' AND TIMESTAMPDIFF(MINUTE, b.createdDate, CURRENT_TIMESTAMP) < 15)) " +
                    "AND rt.isDorm = false " +
                    "AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate AND hs.id = :homestayId) " +
            "GROUP BY hrt.id, rt.roomTypeName, hrt.capacity, rt.singleBed, rt.doubleBed, hrt.price, hrt.isChildrenAndBed, rt.isDorm " +
            "ORDER BY hrt.capacity")
    List<RoomTypeHouseholdAvailableDto> getRoomTypeAvailableByHomestay(Long homestayId, LocalDate checkInDate, LocalDate checkOutDate);

    @Query("SELECT new com.example.vhomestay.model.dto.response.booking.customer.RoomTypeHouseholdAvailableDto(hrt.id, rt.roomTypeName, hrt.capacity, rt.singleBed, rt.doubleBed, hrt.price, hrt.isChildrenAndBed, rt.isDorm, COUNT(*)) " +
            "FROM DormSlot ds " +
            "JOIN ds.room r " +
            "JOIN r.householdRoomType hrt " +
            "JOIN hrt.roomType rt " +
            "JOIN r.homestay h " +
            "WHERE h.id = :homestayId AND rt.isDorm = true AND h.status = 'ACTIVE' AND r.status = 'ACTIVE' AND ds.status = 'ACTIVE' AND ds.id NOT IN " +
                    "(SELECT ds.id " +
                    "FROM BookingDetail db " +
                    "JOIN db.dormSlot ds " +
                    "JOIN ds.room r " +
                    "JOIN r.householdRoomType hrt " +
                    "JOIN hrt.roomType rt " +
                    "JOIN r.homestay hs " +
                    "JOIN db.booking b " +
                    "WHERE (db.status = 'BOOKED' OR db.status = 'CHECKED_IN' OR (db.status = 'PENDING' AND TIMESTAMPDIFF(MINUTE, b.createdDate, CURRENT_TIMESTAMP) < 15)) " +
                    "AND rt.isDorm = true " +
                    "AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate AND hs.id = :homestayId) " +
            "GROUP BY hrt.id, rt.roomTypeName, hrt.capacity, rt.singleBed, rt.doubleBed, hrt.price, hrt.isChildrenAndBed, rt.isDorm")
    List<RoomTypeHouseholdAvailableDto> getDormitoryAvailableByHomestay(Long homestayId, LocalDate checkInDate, LocalDate checkOutDate);

    @Query("SELECT r FROM Room r " +
            "JOIN r.householdRoomType hrt " +
            "JOIN hrt.roomType rt " +
            "JOIN r.homestay hs " +
            "JOIN hs.household hh " +
            "WHERE hh.id = :householdId AND hh.status = 'ACTIVE' AND hs.status = 'ACTIVE' AND r.status = 'ACTIVE' AND rt.isDorm = false AND r.id NOT IN " +
            "(SELECT r.id " +
            "FROM BookingDetail db " +
            "JOIN db.room r " +
            "JOIN r.householdRoomType hrt " +
            "JOIN hrt.roomType rt " +
            "JOIN r.homestay hs " +
            "JOIN db.booking b " +
            "JOIN hs.household hh " +
            "WHERE (db.status = 'BOOKED' OR db.status = 'CHECKED_IN' OR (db.status = 'PENDING' AND TIMESTAMPDIFF(MINUTE, b.createdDate, CURRENT_TIMESTAMP) < 15)) " +
            "AND rt.isDorm = false " +
            "AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate AND hh.id = :householdId) " +
            "ORDER BY rt.roomTypeName, r.roomName")
    List<Room> findAllRoomAvailable(Long householdId, LocalDate checkInDate, LocalDate checkOutDate);

    @Query("SELECT ds FROM DormSlot ds " +
            "JOIN ds.room r " +
            "JOIN r.householdRoomType hrt " +
            "JOIN hrt.roomType rt " +
            "JOIN r.homestay hs " +
            "JOIN hs.household hh " +
            "WHERE hh.id = :householdId AND hh.status = 'ACTIVE' AND hs.status = 'ACTIVE' AND r.status = 'ACTIVE' AND ds.status = 'ACTIVE' AND rt.isDorm = true AND ds.id NOT IN " +
            "(SELECT ds.id " +
            "FROM BookingDetail db " +
            "JOIN db.dormSlot ds " +
            "JOIN ds.room r " +
            "JOIN r.householdRoomType hrt " +
            "JOIN hrt.roomType rt " +
            "JOIN r.homestay hs " +
            "JOIN db.booking b " +
            "JOIN hs.household hh " +
            "WHERE (db.status = 'BOOKED' OR db.status = 'CHECKED_IN' OR (db.status = 'PENDING' AND TIMESTAMPDIFF(MINUTE, b.createdDate, CURRENT_TIMESTAMP) < 15)) " +
            "AND rt.isDorm = true " +
            "AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate AND hh.id = :householdId) " +
            "ORDER BY r.roomName, ds.slotNumber")
    List<DormSlot> findAllDormSlotAvailable(Long householdId, LocalDate checkInDate, LocalDate checkOutDate);

    @Query("SELECT new com.example.vhomestay.model.dto.response.bookingdetail.BookingDetailCheckInOutTodayResponseDto(h.id, h.homestayCode, rt.id, " +
            " rt.roomTypeName, r.id, r.roomName, COUNT(*), rt.isDorm ) " +
            "FROM Booking b JOIN b.bookingDetails bd JOIN bd.homestay h JOIN bd.room r JOIN r.householdRoomType hrt JOIN hrt.roomType rt " +
            "WHERE b.bookingCode = :bookingCode AND b.status != 'CANCELLED' " +
            "GROUP BY h.id, rt.id, r.id")
    List<BookingDetailCheckInOutTodayResponseDto> findBookingDetailCheckInOut(@Param("bookingCode") String bookingCode);
}

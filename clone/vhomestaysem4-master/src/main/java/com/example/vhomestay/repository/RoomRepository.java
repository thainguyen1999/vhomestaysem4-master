package com.example.vhomestay.repository;

import com.example.vhomestay.model.dto.response.DormInformationResponseDto;
import com.example.vhomestay.model.dto.response.RoomInformationResponseDto;
import com.example.vhomestay.model.dto.response.dashboard.manager.DormitoryInformationForDashboard;
import com.example.vhomestay.model.dto.response.dashboard.manager.RoomInformationForDashboard;
import com.example.vhomestay.model.dto.response.room.RoomSearchManagerResponseDto;
import com.example.vhomestay.model.dto.response.room.RoomTodayManagerResponseDto;
import com.example.vhomestay.model.entity.Room;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends CrudRepository<Room, Long>, BaseRepository<Room, Long> {
    Optional<Room> findById(Long roomId);
    List<Room> findAllByHomestayId(Long homestayId);
    @Query("select r from Room r join r.homestay h join r.householdRoomType hsrt join hsrt.roomType rt " +
            "where h.homestayCode = :homestayCode and hsrt.id = :householdRoomTypeId and r.status != 'DELETED'")
    List<Room> getAllRoomByHomestayCodeAndHouseholdRoomType(String homestayCode, Long householdRoomTypeId);
    @Query("select new com.example.vhomestay.model.dto.response.RoomInformationResponseDto(r.id, hs.homestayCode, r.roomName, " +
            "rt.roomTypeName, hrt.capacity, hrt.price, hrt.priceUpdate, r.status)" +
            "from Room r join r.homestay hs join r.householdRoomType hrt join hrt.roomType rt " +
            "join hs.household h join h.manager m join m.account a " +
            "where a.email = :managerEmail and r.status != 'DELETED' " +
            "and rt.isDorm = false " +
            "order by hs.homestayCode asc")
    List<RoomInformationResponseDto> findRoomByManagerEmail(@Nullable String managerEmail);
    @Query("select new com.example.vhomestay.model.dto.response.DormInformationResponseDto(r.id, hs.homestayCode, hrt.price," +
            "r.roomName, r.status)" +
            "from Room r join r.homestay hs join r.householdRoomType hrt join hrt.roomType rt " +
            "join hs.household h join h.manager m join m.account a " +
            "where a.email = :managerEmail and r.status != 'DELETED' and rt.isDorm = true" +
            " order by hs.homestayCode asc")
    List<DormInformationResponseDto> findDormByManagerEmail(@Nullable String managerEmail);
    @Query("select count(r) from Room r join r.homestay h join r.householdRoomType hsrt join hsrt.roomType rt  " +
            "where h.id = :homestayId and rt.isDorm = false and r.status != 'DELETED'")
    Integer countAllRoomByHomestayId(Long homestayId);
    @Query("select count(ds) from DormSlot ds " +
            "join ds.room r " +
            "join r.homestay h " +
            "where h.id = :homestayId and ds.status != 'DELETED' and r.status = 'ACTIVE'")
    Integer countAllDormByHomestayId(Long homestayId);
    @Query("select count(dl) from DormSlot dl join dl.room r where r.id = :roomId and dl.status != 'DELETED'")
    Integer countDormSlotByRoomId(Long roomId);
    @Query("select count(r) from Room r join r.householdRoomType hsrt join hsrt.roomType rt" +
            " join r.homestay hs join hs.household h join h.manager m join m.account a " +
            "where a.email = :managerEmail and rt.isDorm = false and r.status != 'DELETED'")
    Integer countRoomByHousehold(String managerEmail);
    @Query("select count(dl) from DormSlot dl join dl.room r join r.householdRoomType hsrt join hsrt.roomType rt" +
            " join r.homestay hs join hs.household h join h.manager m join m.account a " +
            "where a.email = :managerEmail and rt.isDorm = true and r.status != 'DELETED' and dl.status != 'DELETED'")
    Integer countDormSlotByHousehold(String managerEmail);
    @Query(nativeQuery = true,
            value = "WITH t as (SELECT bd.room_id, bd.dorm_slot_id " +
                    "FROM hbs_db.booking b " +
                    "JOIN hbs_db.household h ON b.household_id = h.id " +
                    "    JOIN hbs_db.manager m ON h.manager_id = m.id " +
                    "    JOIN hbs_db.account a ON m.account_id = a.id " +
                    "    JOIN hbs_db.booking_detail bd ON b.booking_code = bd.booking_code " +
                    "    JOIN hbs_db.room r ON bd.room_id = r.id " +
                    "    LEFT JOIN hbs_db.dorm_slot ds ON bd.dorm_slot_id = ds.id " +
                    "WHERE a.email = :email " +
                    "AND b.status = 'BOOKED' OR b.status = 'CHECKED_IN' OR (b.status = 'PENDING' AND TIMESTAMPDIFF(MINUTE, b.created_date, CURRENT_TIMESTAMP) < 15) " +
//                    "AND b.status IN ('BOOKED', 'CHECKED_IN') " +
                    "AND bd.status IN ('BOOKED', 'CHECKED_IN') " +
                    "    AND b.check_in_date < :checkOutDate AND b.check_out_date > :checkInDate) " +
                    "(SELECT h.id, h.homestay_code, hrt.id, rt.room_type_name, " +
                    "COUNT(ds.id) AS total_slot, r.id, r.room_name, hrt.capacity, " +
                    "        rt.single_bed, rt.double_bed, hrt.price, rt.is_dorm " +
                    "FROM hbs_db.room r " +
                    "LEFT JOIN hbs_db.dorm_slot ds ON r.id = ds.room_id " +
                    "    JOIN hbs_db.homestay h ON h.id = r.homestay_id " +
                    "    JOIN hbs_db.household hh ON hh.id = h.household_id " +
                    "   JOIN hbs_db.manager m ON hh.manager_id = m.id" +
                    "    JOIN hbs_db.account a ON m.account_id = a.id " +
                    "    JOIN hbs_db.household_room_type hrt ON hrt.id = r.household_room_type_id " +
                    "    JOIN hbs_db.room_type rt ON rt.id = hrt.room_type_id " +
                    "WHERE a.email = :email AND (:homestayId IS NULL OR h.id = :homestayId) AND hh.status = 'ACTIVE' AND h.status = 'ACTIVE' AND r.status = 'ACTIVE' " +
                    "AND r.id NOT IN (SELECT t.room_id FROM t WHERE t.dorm_slot_id IS NULL) " +
                    "AND rt.is_dorm = 0 " +
                    "GROUP BY (r.id) ) " +
                    "UNION " +
                    "(SELECT h.id, h.homestay_code, hrt.id, rt.room_type_name, " +
                    "COUNT(ds.id) AS total_slot, r.id, r.room_name, hrt.capacity, " +
                    "        rt.single_bed, rt.double_bed, hrt.price, rt.is_dorm " +
                    "FROM hbs_db.room r " +
                    "LEFT JOIN hbs_db.dorm_slot ds ON r.id = ds.room_id " +
                    "    JOIN hbs_db.homestay h ON h.id = r.homestay_id " +
                    "    JOIN hbs_db.household hh ON hh.id = h.household_id " +
                    "    JOIN hbs_db.manager m ON hh.manager_id = m.id " +
                    "    JOIN hbs_db.account a ON m.account_id = a.id " +
                    "    JOIN hbs_db.household_room_type hrt ON hrt.id = r.household_room_type_id " +
                    "    JOIN hbs_db.room_type rt ON rt.id = hrt.room_type_id " +
                    "WHERE a.email = :email AND (:homestayId IS NULL OR h.id = :homestayId) AND hh.status = 'ACTIVE' AND h.status = 'ACTIVE' AND r.status = 'ACTIVE' AND ds.status = 'ACTIVE' " +
                    "AND ds.id NOT IN (SELECT t.dorm_slot_id FROM t WHERE t.dorm_slot_id IS NOT NULL) " +
                    "AND rt.is_dorm = 1 " +
                    "GROUP BY (r.id)) ")
    List<Object[]> searchAvailableRoomsWithTotalDormSlotByManager(@Param("email") String managerEmail,
                                                 @Param("homestayId") Long homestayId,
                                                 @Param("checkInDate") LocalDate checkInDate,
                                                 @Param("checkOutDate") LocalDate checkOutDate);
    @Query("SELECT new com.example.vhomestay.model.dto.response.dashboard.manager.RoomInformationForDashboard(r.id, r.roomName, rt.roomTypeName, false, b.bookingCode, bd.checkInCustomerName) " +
            "FROM Room r " +
            "JOIN r.householdRoomType hrt " +
            "JOIN hrt.roomType rt " +
            "JOIN r.homestay hs " +
            "JOIN r.bookingDetails bd " +
            "JOIN bd.booking b " +
            "WHERE rt.isDorm = false " +
            "AND hs.id = :homestayId " +
            "AND r.status != 'DELETED' AND r.status != 'INACTIVE' " +
            "AND bd.status != 'DELETED' AND bd.status != 'PENDING' AND bd.status != 'CANCELLED' " +
            "AND b.status != 'DELETED' AND b.status != 'PENDING' AND b.status != 'CANCELLED' " +
            "AND b.checkInDate <= :date AND b.checkOutDate > :date ")
    List<RoomInformationForDashboard> getRoomLockedInformationForDashboard(Long homestayId, LocalDate date);

    @Query("SELECT new com.example.vhomestay.model.dto.response.dashboard.manager.RoomInformationForDashboard(r.id, r.roomName, rt.roomTypeName, true, '', '') " +
            "FROM Room r " +
            "JOIN r.householdRoomType hrt " +
            "JOIN hrt.roomType rt " +
            "JOIN r.homestay hs " +
            "WHERE rt.isDorm = false " +
            "AND hs.id = :homestayId " +
            "AND r.status != 'DELETED' AND r.status != 'INACTIVE'")
    List<RoomInformationForDashboard> getAllRoomInformationForDashboard(Long homestayId);

    @Query(" SELECT new com.example.vhomestay.model.dto.response.dashboard.manager.DormitoryInformationForDashboard(r.id, r.roomName, rt.roomTypeName, 0, COUNT(ds.id)) " +
            "FROM Room r " +
            "JOIN r.dormSlots ds " +
            "JOIN r.householdRoomType hrt " +
            "JOIN hrt.roomType rt " +
            "JOIN r.homestay hs " +
            "JOIN r.bookingDetails bd " +
            "JOIN bd.booking b " +
            "WHERE rt.isDorm = true " +
            "AND hs.id = :homestayId " +
            "AND r.status != 'DELETED' AND r.status != 'INACTIVE' " +
            "AND ds.status != 'DELETED' AND ds.status != 'INACTIVE' " +
            "AND bd.status != 'DELETED' AND bd.status != 'PENDING' AND bd.status != 'CANCELLED' " +
            "AND b.status != 'DELETED' AND b.status != 'PENDING' AND b.status != 'CANCELLED' " +
            "AND b.checkInDate <= :date AND b.checkOutDate > :date " +
            "GROUP BY (r.id) ")
    List<DormitoryInformationForDashboard> getDormitoryLockedInformationForDashboard(Long homestayId, LocalDate date);

    @Query("SELECT new com.example.vhomestay.model.dto.response.dashboard.manager.DormitoryInformationForDashboard(r.id, r.roomName, rt.roomTypeName, 0, COUNT(ds.id)) " +
            "FROM Room r " +
            "JOIN r.householdRoomType hrt " +
            "JOIN hrt.roomType rt " +
            "JOIN r.homestay hs " +
            "LEFT JOIN DormSlot ds ON r.id = ds.room.id " +
            "WHERE rt.isDorm = true " +
            "AND hs.id = :homestayId " +
            "AND r.status != 'DELETED' AND r.status != 'INACTIVE' " +
            "AND ds.status != 'DELETED' AND ds.status != 'INACTIVE' " +
            "GROUP BY (r.id) ")
    List<DormitoryInformationForDashboard> getAllDormitoryInformationForDashboard(Long homestayId);
}

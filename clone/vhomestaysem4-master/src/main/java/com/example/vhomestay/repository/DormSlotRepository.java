package com.example.vhomestay.repository;

import com.example.vhomestay.enums.BaseStatus;
import com.example.vhomestay.model.entity.DormSlot;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DormSlotRepository extends BaseRepository<DormSlot, Long> {
    @Query("SELECT COUNT(d) FROM DormSlot d join d.room r WHERE r.id = ?1 AND d.status = 'ACTIVE'")
    Integer findFinalIndexByRoomId(Long roomId);
    @Query("SELECT d FROM DormSlot d join d.room r WHERE r.id = ?1 AND d.status != 'DELETED' ORDER BY d.slotNumber DESC LIMIT ?2")
    List<DormSlot> findDormSlotFinalIndexByRoomIdAndNumberOfSlots(Long roomId, Integer numberOfSlots);
    @Query("SELECT COUNT(d) FROM DormSlot d join d.room r WHERE r.id = ?1 AND d.status != 'DELETED'")
    Integer countDormSlotByRoomId(Long roomId);
    @Query("SELECT ds FROM BookingDetail bd JOIN bd.booking b JOIN bd.dormSlot ds JOIN bd.room r WHERE r.id = :roomId AND b.bookingCode = :bookingCode")
    List<DormSlot> findDormSlotByRoomIdAndBookingCode(@Param("roomId") Long roomId, @Param("bookingCode") String bookingCode);

    @Modifying
    @Query("UPDATE DormSlot d SET d.status = :status WHERE d.room.id = :roomId")
    void updateStatusByRoomId(@Param("roomId") Long roomId, @Param("status") BaseStatus status);
}

package com.example.vhomestay.repository;

import com.example.vhomestay.model.entity.RoomType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomTypeRepository extends BaseRepository<RoomType, Long> {
    @Query("SELECT rt FROM RoomType rt WHERE rt.id NOT IN " +
            "(SELECT rt.id FROM HouseholdRoomType hrt join hrt.roomType rt join hrt.household h WHERE h.id = ?1)")
    List<RoomType> findRoomTypeNotInHouseholdRoomType(Long householdId);
    @Query("SELECT count(rt) FROM RoomType rt")
    Integer countAllRoomType();
}

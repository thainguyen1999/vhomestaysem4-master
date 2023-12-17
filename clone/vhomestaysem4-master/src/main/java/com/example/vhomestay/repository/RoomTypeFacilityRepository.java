package com.example.vhomestay.repository;

import com.example.vhomestay.model.entity.Facility;
import com.example.vhomestay.model.entity.RoomTypeFacility;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomTypeFacilityRepository extends BaseRepository<RoomTypeFacility, Long> {
    @Query("select f from Facility f join f.roomFacility rtf join rtf.householdRoomType hrt where hrt.id = ?1")
    List<Facility> findFacilitiesByHouseholdRoomTypeId(Long id);

    @Query("select rtf from RoomTypeFacility rtf join rtf.householdRoomType hrt where hrt.id = ?1")
    List<RoomTypeFacility> findAllByHouseholdRoomTypeId(Long id);

    @Query("select f.facilityName from Facility f join f.roomFacility rtf join rtf.householdRoomType hrt where hrt.id = :id")
    List<String> findFacilityByRoomTypeId(Long id);
}

package com.example.vhomestay.repository;

import com.example.vhomestay.model.entity.HomestayMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HomestayMediaRepository extends JpaRepository<HomestayMedia, Long>, BaseRepository<HomestayMedia, Long>{
    @Query("select hm from HomestayMedia hm join hm.homestay hs where hs.id = :homestayId")
    List<HomestayMedia> findAllByHomestayId(Long homestayId);

    @Query("select hm.filePath from HomestayMedia hm join hm.homestay hs where hs.id = :id")
    List<String> findImageUriByHomestayId(Long id);

    @Query("select hm.filePath from HomestayMedia hm join hm.householdRoomType hrt where hrt.id = :roomTypeHouseholdId")
    List<String> findImageUriByRoomTypeId(Long roomTypeHouseholdId);
}

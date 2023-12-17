package com.example.vhomestay.repository;

import com.example.vhomestay.enums.MediaVillagePosition;
import com.example.vhomestay.model.entity.VillageMedia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VillageMediaRepository extends BaseRepository<VillageMedia, Long>{
    @Query("SELECT v.filePath FROM VillageMedia v " +
            "WHERE v.position IN (com.example.vhomestay.enums.MediaVillagePosition.HOME_MAIN, com.example.vhomestay.enums.MediaVillagePosition.HOME_SUB, com.example.vhomestay.enums.MediaVillagePosition.GALLERY) " +
            "ORDER BY v.position ASC")
    List<String> getAllUrlVillageMedia();

    @Query("SELECT v FROM VillageMedia v WHERE v.position = :position")
    List<VillageMedia> findByVillageMediaByPosition(MediaVillagePosition position);

    @Query("SELECT v.filePath FROM VillageMedia v " +
            "WHERE v.position = com.example.vhomestay.enums.MediaVillagePosition.HOME_MAIN OR v.position = com.example.vhomestay.enums.MediaVillagePosition.HOME_SUB " +
            "ORDER BY v.position ASC")
    List<String> getVillageMediaHomePage();
    List<VillageMedia> findAllByLocalProductId(Long localProductId);
    @Query("SELECT v FROM VillageMedia v WHERE v.villageInformation.id = :id")
    List<VillageMedia> findAllByVillageInformationId(Long id);
}

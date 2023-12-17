package com.example.vhomestay.repository;

import com.example.vhomestay.enums.VillageInformationType;
import com.example.vhomestay.model.entity.VillageInformation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VillageInformationRepository extends BaseRepository<VillageInformation, Long>{
    @Query("SELECT v FROM VillageInformation v WHERE v.type = :type")
    Optional<VillageInformation> findByType(@Param("type") VillageInformationType type);

}

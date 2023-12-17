package com.example.vhomestay.repository;

import com.example.vhomestay.model.entity.Facility;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacilityRepository extends BaseRepository<Facility, Long>{
    @Query("select f from Facility f where f.status = 'ACTIVE'")
    List<Facility> findAllActive();
    @Query("select f from Facility f where f.id = :id and f.status = 'ACTIVE'")
    Optional<Facility> findByIdActive(@Param("id") Long id);
}

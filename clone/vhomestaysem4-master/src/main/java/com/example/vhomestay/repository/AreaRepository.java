package com.example.vhomestay.repository;

import com.example.vhomestay.model.entity.Area;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AreaRepository extends BaseRepository<Area, Long>{
    @Query("SELECT a FROM Area a WHERE a.name = :name AND a.status != 'DELETED'")
    Optional<Area> findAreByName(@Param("name") String name);
    @Query("SELECT a FROM Area a WHERE a.status != 'DELETED'")
    List<Area> getAreasByAdmin();
    @Query("SELECT a FROM Area a WHERE a.id = :areaId AND a.status != 'DELETED'")
    Optional<Area> getAreaByAdmin(@Param("areaId") Long areaId);
    @Query("SELECT count(a) FROM Area a WHERE a.status != 'DELETED'")
    Integer countAllArea();
}

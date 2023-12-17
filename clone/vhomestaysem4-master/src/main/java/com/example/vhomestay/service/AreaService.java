package com.example.vhomestay.service;

import com.example.vhomestay.model.dto.request.area.AreaAdminRequestDto;
import com.example.vhomestay.model.dto.response.area.AreaAdminResponseDto;
import com.example.vhomestay.model.dto.response.household.customer.HomestayIntroductionDto;
import com.example.vhomestay.model.entity.Area;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface AreaService extends BaseService<Area, Long>{
    boolean addAreaByAdmin(AreaAdminRequestDto area) throws IOException;
    List<AreaAdminResponseDto> getAreasByAdmin();
    Optional<AreaAdminResponseDto> getAreaByAdmin(Long areaId);
    boolean updateAreaByAdmin(AreaAdminRequestDto area);
    boolean deleteAreaByAdmin(Long areaId);
    List<HomestayIntroductionDto> getAreaIntroductionList();
}

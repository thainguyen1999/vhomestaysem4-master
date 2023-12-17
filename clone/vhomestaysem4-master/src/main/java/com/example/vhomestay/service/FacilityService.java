package com.example.vhomestay.service;

import com.example.vhomestay.model.dto.response.FacilityDto;
import com.example.vhomestay.model.dto.response.facility.FacilityAdminResponseDto;
import com.example.vhomestay.model.entity.Facility;

import java.util.List;

public interface FacilityService {
    List<Facility> findAll();

    FacilityDto mapFacilityToDto(Facility facility);

    Facility mapDtoToFacility(FacilityDto facilityDto);

    List<FacilityDto> mapFacilityListToDtoList(List<Facility> facilityList);
    List<Facility> mapDtoListToFacilityList(List<FacilityDto> facilityDtoList);
    List<FacilityAdminResponseDto> findAllActive();
    void updateFacilityByAdmin(FacilityAdminResponseDto facilityAdminResponseDto);
    void createFacilityByAdmin(FacilityAdminResponseDto facilityAdminResponseDto);

    void deleteFacilityByAdmin(Long id);
}

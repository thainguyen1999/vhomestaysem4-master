package com.example.vhomestay.service;

import com.example.vhomestay.model.dto.response.FacilityDto;
import com.example.vhomestay.model.dto.response.HomestayMediaDto;
import com.example.vhomestay.model.entity.Facility;
import com.example.vhomestay.model.entity.HomestayMedia;

import java.util.List;

public interface HomestayMediaService {
    HomestayMediaDto mapHomestayMediaToDto(HomestayMedia homestayMedia);

    HomestayMedia mapDtoToHomestayMedia(HomestayMediaDto homestayMediaDto);

    List<HomestayMediaDto> mapHomestayMediaListToDtoList(List<HomestayMedia> homestayMediaList);

    List<HomestayMedia> mapDtoListToHomestayMediaList(List<HomestayMediaDto> homestayMediaDtoList);

}

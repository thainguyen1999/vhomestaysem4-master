package com.example.vhomestay.service.impl;

import com.example.vhomestay.enums.BaseStatus;
import com.example.vhomestay.model.dto.request.area.AreaAdminRequestDto;
import com.example.vhomestay.model.dto.response.area.AreaAdminResponseDto;
import com.example.vhomestay.model.dto.response.homestay.admin.HomestayAreaAdminResponseDto;
import com.example.vhomestay.model.dto.response.household.customer.HomestayIntroductionDto;
import com.example.vhomestay.model.entity.Area;
import com.example.vhomestay.model.entity.Homestay;
import com.example.vhomestay.repository.AreaRepository;
import com.example.vhomestay.repository.HomestayRepository;
import com.example.vhomestay.service.AreaService;
import com.example.vhomestay.service.StorageService;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import com.example.vhomestay.util.exception.ResourceInternalServerErrorException;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AreaServiceImpl extends BaseServiceImpl<Area, Long, AreaRepository>
        implements AreaService {
    private final AreaRepository areaRepository;
    private final HomestayRepository homestayRepository;
    private final StorageService storageService;

    @Override
    public boolean addAreaByAdmin(AreaAdminRequestDto areaCreateAdminRequestDto) {
        try {
            Area area = new Area();
            area.setName(areaCreateAdminRequestDto.getName());
            String imageUrl = storageService.uploadFile(areaCreateAdminRequestDto.getImage());
            area.setImage(imageUrl);
            area.setStatus(BaseStatus.ACTIVE);
            areaRepository.save(area);
            return true;
        } catch (IOException e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public List<AreaAdminResponseDto> getAreasByAdmin() {
        List<Area> areas = areaRepository.getAreasByAdmin();
        List<AreaAdminResponseDto> areaAdminResponseDtos = new ArrayList<>();
        List<HomestayAreaAdminResponseDto> homestayDtos;

        AreaAdminResponseDto areaAdminResponseDto;
        HomestayAreaAdminResponseDto homestayDto;
        List<Homestay> homestays;
        for (Area area : areas) {
            areaAdminResponseDto = new AreaAdminResponseDto();
            areaAdminResponseDto.setId(area.getId());
            areaAdminResponseDto.setName(area.getName());
            areaAdminResponseDto.setImage(area.getImage());

//            homestays = homestayService.getHomestaysByAreaId(area.getId());
            homestays = homestayRepository.findHomestaysByAreaId(area.getId());

            homestayDtos = new ArrayList<>();
            for (Homestay homestay : homestays) {
                homestayDto = new HomestayAreaAdminResponseDto();
                homestayDto.setHomestayId(homestay.getId());
                homestayDto.setHomestayCode(homestay.getHomestayCode());
                homestayDto.setHouseholdId(homestay.getHousehold().getId());
                homestayDto.setHouseholdName(homestay.getHousehold().getHouseholdName());

                homestayDtos.add(homestayDto);
            }
            areaAdminResponseDto.setHomestays(homestayDtos);
            areaAdminResponseDto.setTotalHomestay(homestayDtos.size());

            areaAdminResponseDtos.add(areaAdminResponseDto);
        }

        return areaAdminResponseDtos;
    }

    @Override
    public Optional<AreaAdminResponseDto> getAreaByAdmin(Long areaId) {
        Optional<Area> areaOptional = areaRepository.getAreaByAdmin(areaId);

        if (areaOptional.isPresent()) {
            Area area = areaOptional.get();
            AreaAdminResponseDto areaAdminResponseDto = new AreaAdminResponseDto();
            areaAdminResponseDto.setId(area.getId());
            areaAdminResponseDto.setName(area.getName());
            areaAdminResponseDto.setImage(area.getImage());

            List<Homestay> homestays = homestayRepository.findHomestaysByAreaId(area.getId());
            List<HomestayAreaAdminResponseDto> homestayDtos = new ArrayList<>();
            HomestayAreaAdminResponseDto homestayDto;

            for (Homestay homestay : homestays) {
                homestayDto = new HomestayAreaAdminResponseDto();
                homestayDto.setHomestayId(homestay.getId());
                homestayDto.setHomestayCode(homestay.getHomestayCode());
                homestayDto.setHouseholdId(homestay.getHousehold().getId());
                homestayDto.setHouseholdName(homestay.getHousehold().getHouseholdName());

                homestayDtos.add(homestayDto);
            }
            areaAdminResponseDto.setHomestays(homestayDtos);
            areaAdminResponseDto.setTotalHomestay(homestayDtos.size());

            return Optional.of(areaAdminResponseDto);
        }
        return Optional.empty();
    }

    @Override
    public boolean updateAreaByAdmin(AreaAdminRequestDto areaAdminRequestDto) {
        Optional<Area> areaOptional = areaRepository.getAreaByAdmin(areaAdminRequestDto.getId());

        if (areaOptional.isEmpty()) {
            throw new ResourceNotFoundException("area.not.found");
        }

        Area area = areaOptional.get();
        if(areaAdminRequestDto.getName() != null) {
            area.setName(areaAdminRequestDto.getName());
        }
        try {
            if(areaAdminRequestDto.getImage() != null && !areaAdminRequestDto.getImage().isEmpty()) {
                String oldImageUrl = area.getImage();
                String newImageUrl = storageService.updateFile(oldImageUrl, areaAdminRequestDto.getImage());
                area.setImage(newImageUrl);
            }

            areaRepository.save(area);
            return true;
        } catch (IOException e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public boolean deleteAreaByAdmin(Long areaId) {
        Optional<Area> areaOptional = areaRepository.getAreaByAdmin(areaId);

        if (areaOptional.isEmpty()) {
            throw new ResourceNotFoundException("area.not.found");
        }

        Area area = areaOptional.get();

        List<Homestay> homestays = homestayRepository.findHomestaysByAreaId(areaId);
//        List<Homestay> homestays = homestayService.getHomestaysByAreaId(areaId);
        if (!homestays.isEmpty()) {
            throw new ResourceBadRequestException("Bạn không thể xóa " + area.getName() + " vì đang có " + homestays.size() + " homestay đang hoạt động tại đây!");
        }

        try{
            area.setStatus(BaseStatus.DELETED);
            storageService.deleteFile(area.getImage());
            area.setImage(null);
            areaRepository.save(area);
            return true;
        }catch (Exception e){
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public List<HomestayIntroductionDto> getAreaIntroductionList() {
        return homestayRepository.getAllHomestayIntroduction();
    }
}

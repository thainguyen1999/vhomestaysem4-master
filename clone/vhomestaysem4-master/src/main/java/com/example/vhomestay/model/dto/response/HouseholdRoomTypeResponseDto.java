package com.example.vhomestay.model.dto.response;

import com.example.vhomestay.enums.HouseholdTypeRoomStatus;
import com.example.vhomestay.model.entity.Facility;
import com.example.vhomestay.model.entity.HomestayMedia;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseholdRoomTypeResponseDto {
    private Long householdRoomTypeId;
    private String roomTypeName;
    private Integer capacity;
    private BigDecimal price;
    private BigDecimal priceUpdate;
    private Integer singleBed;
    private Integer doubleBed;
    private Boolean isChildrenAndBed;
    private List<FacilityDto> facilities;
    private List<HomestayMedia> homestayMedias;
    private List<MultipartFile> imageFiles;
    private List<FacilityDto> facilityVillageList;
    private HouseholdTypeRoomStatus status;
}

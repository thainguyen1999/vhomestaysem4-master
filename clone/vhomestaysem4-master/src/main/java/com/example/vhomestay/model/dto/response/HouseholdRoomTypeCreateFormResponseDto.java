package com.example.vhomestay.model.dto.response;

import com.example.vhomestay.model.entity.Facility;
import com.example.vhomestay.model.entity.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HouseholdRoomTypeCreateFormResponseDto {
    private List<RoomTypeDto> roomTypes;
    private BigDecimal priceUpdate;
    private Integer capacity;
    private Boolean isChildrenAndBed;
    private List<FacilityDto> facilities;
    private List<MultipartFile> mediaFiles;
}

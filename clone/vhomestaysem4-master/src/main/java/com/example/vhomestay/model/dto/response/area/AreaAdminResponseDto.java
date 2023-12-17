package com.example.vhomestay.model.dto.response.area;

import com.example.vhomestay.model.dto.response.homestay.admin.HomestayAreaAdminResponseDto;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class AreaAdminResponseDto {
    private Long id;
    private String name;
    private String image;
    private Integer totalHomestay;
    private List<HomestayAreaAdminResponseDto> homestays;
}

package com.example.vhomestay.model.dto.response.homestay.admin;

import com.example.vhomestay.enums.HomestayStatus;
import com.example.vhomestay.model.dto.response.HomestayMediaDto;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class HomestayCommonAdminResponseDto {
    private Long id;
    private String homestayCode;
    private Integer capacityOfHomestay;
    private Integer totalRoomOfHomestay;
    private Integer totalDormOfHomestay;
    private String fullAddress;
    private List<HomestayMediaDto> homestayMediaDtoList;
    private HomestayStatus homestayStatus;
    private String householdName;
    private String householderName;
    private String householdPhone;
    private String householdEmail;
}

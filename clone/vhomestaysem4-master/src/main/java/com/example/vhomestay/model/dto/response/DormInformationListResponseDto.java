package com.example.vhomestay.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DormInformationListResponseDto {
    private List<DormInformationResponseDto> dormInformationResponseDtoList;
    private Integer totalRoom;
}

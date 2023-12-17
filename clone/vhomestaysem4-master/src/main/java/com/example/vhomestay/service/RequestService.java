package com.example.vhomestay.service;

import com.example.vhomestay.model.dto.request.request.RequestUpdateStatusDto;
import com.example.vhomestay.model.dto.response.request.RequestDetailResponseDto;
import com.example.vhomestay.model.dto.response.request.RequestForManagerDto;
import com.example.vhomestay.model.dto.response.request.RequestResponseDto;
import com.example.vhomestay.model.dto.response.request.RoomTypeHouseholdUpdatePriceDto;
import com.example.vhomestay.model.entity.Request;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RequestService extends BaseService<Request, Long> {

    List<RequestResponseDto> getRequestByAdmin();

    RequestDetailResponseDto getRequestDetailByAdmin(Long requestId);

    RoomTypeHouseholdUpdatePriceDto getRoomTypeHouseholdUpdatePriceDto(Long requestId);

    @Transactional
    void updateRequestByAdmin(RequestUpdateStatusDto requestUpdateStatusDto);

    List<RequestForManagerDto> getRequestByManager();

    @Transactional
    void deleteRequestByManager(Long requestId);
}

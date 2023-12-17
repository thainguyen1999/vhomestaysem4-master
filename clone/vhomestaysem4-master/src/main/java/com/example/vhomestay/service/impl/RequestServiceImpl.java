package com.example.vhomestay.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.vhomestay.constant.DateTimeConstant;
import com.example.vhomestay.enums.HouseholdTypeRoomStatus;
import com.example.vhomestay.enums.RequestStatus;
import com.example.vhomestay.model.dto.request.NotificationRequestPriceRequest;
import com.example.vhomestay.model.dto.request.request.RequestUpdateStatusDto;
import com.example.vhomestay.model.dto.response.RoomPriceUpdateDto;
import com.example.vhomestay.model.dto.response.request.RequestDetailResponseDto;
import com.example.vhomestay.model.dto.response.request.RequestForManagerDto;
import com.example.vhomestay.model.dto.response.request.RequestResponseDto;
import com.example.vhomestay.model.dto.response.request.RoomTypeHouseholdUpdatePriceDto;
import com.example.vhomestay.model.entity.Admin;
import com.example.vhomestay.model.entity.HouseholdRoomType;
import com.example.vhomestay.model.entity.Request;
import com.example.vhomestay.repository.*;
import com.example.vhomestay.security.SecurityUtil;
import com.example.vhomestay.service.NotificationService;
import com.example.vhomestay.service.RequestService;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import com.example.vhomestay.util.exception.ResourceInternalServerErrorException;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service("requestService")
@RequiredArgsConstructor

public class RequestServiceImpl extends BaseServiceImpl<Request, Long, RequestRepository> implements RequestService {
    private final RequestRepository requestRepository;
    private final HouseholdRoomTypeRepository householdRoomTypeRepository;
    private final HomestayMediaRepository homestayMediaRepository;
    private final RoomTypeFacilityRepository roomTypeFacilityRepository;
    private final AdminRepository adminRepository;
    private final NotificationService notificationService;
    @Override
    public List<RequestResponseDto> getRequestByAdmin() {
        List<RequestResponseDto> requestsPending = requestRepository.getRequestPendingByAdmin();
        List<RequestResponseDto> requestsSolved = requestRepository.getRequestSolvedByAdmin();
        requestsPending.addAll(requestsSolved);
        requestsPending.forEach(requestResponseDto -> requestResponseDto.setCreatedDate(requestResponseDto.getCreatedDate().plusHours(DateTimeConstant.HOURS_DEPLOY)));
        return requestsPending;
    }

    @Override
    public RequestDetailResponseDto getRequestDetailByAdmin(Long requestId) {
        RequestStatus requestStatus = requestRepository.getRequestStatusByAdmin(requestId);
        RequestDetailResponseDto requestDetailResponseDto;
        switch (requestStatus) {
            case PENDING -> requestDetailResponseDto = requestRepository.getRequestDetailPendingByAdmin(requestId);
            case APPROVED, REJECTED -> requestDetailResponseDto = requestRepository.getRequestDetailSolvedByAdmin(requestId);
            default ->
                    throw new ResourceInternalServerErrorException("request.not.found");
        }
        requestDetailResponseDto.setCreatedDate(requestDetailResponseDto.getCreatedDate().plusHours(DateTimeConstant.HOURS_DEPLOY));
        return requestDetailResponseDto;
    }

    @Override
    public RoomTypeHouseholdUpdatePriceDto getRoomTypeHouseholdUpdatePriceDto(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new ResourceNotFoundException("request.not.found"));
        ObjectMapper objectMapper = new ObjectMapper();
        RoomTypeHouseholdUpdatePriceDto roomTypeHouseholdUpdatePriceDto = new RoomTypeHouseholdUpdatePriceDto();
        try {
            RoomPriceUpdateDto priceUpdateDto = objectMapper.readValue(request.getRequestData(), RoomPriceUpdateDto.class);
            HouseholdRoomType householdRoomType = householdRoomTypeRepository.findById(priceUpdateDto.getHouseholdRoomTypeId()).orElseThrow(() -> new ResourceNotFoundException("household.room.type.not.found"));
            roomTypeHouseholdUpdatePriceDto.setRoomTypeHouseholdId(priceUpdateDto.getHouseholdRoomTypeId());
            roomTypeHouseholdUpdatePriceDto.setRoomTypeName(priceUpdateDto.getRoomTypeName());
            roomTypeHouseholdUpdatePriceDto.setCapacity(householdRoomType.getCapacity());
            roomTypeHouseholdUpdatePriceDto.setSingleBed(householdRoomType.getRoomType().getSingleBed());
            roomTypeHouseholdUpdatePriceDto.setDoubleBed(householdRoomType.getRoomType().getDoubleBed());
            roomTypeHouseholdUpdatePriceDto.setIsChildrenBed(householdRoomType.getIsChildrenAndBed());
            roomTypeHouseholdUpdatePriceDto.setPrice(priceUpdateDto.getPrice());
            roomTypeHouseholdUpdatePriceDto.setPriceUpdate(priceUpdateDto.getPriceUpdate());
            roomTypeHouseholdUpdatePriceDto.setImageListUri(homestayMediaRepository.findImageUriByRoomTypeId(priceUpdateDto.getHouseholdRoomTypeId()));
            roomTypeHouseholdUpdatePriceDto.setFacilities(roomTypeFacilityRepository.findFacilityByRoomTypeId(priceUpdateDto.getHouseholdRoomTypeId()));
        } catch (JsonProcessingException e) {
            throw new ResourceBadRequestException("request.data.error");
        }
        return roomTypeHouseholdUpdatePriceDto;
    }

    @Override
    public void updateRequestByAdmin(RequestUpdateStatusDto requestUpdateStatusDto) {
        Request request = requestRepository.findById(requestUpdateStatusDto.getRequestId())
                .filter(r -> r.getRequestStatus().equals(RequestStatus.PENDING))
                .orElseThrow(() -> new ResourceNotFoundException("request.not.found"));
        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new ResourceNotFoundException("user.not.found"));
        Admin admin = adminRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("admin.not.found"));
        try {
            // update price
            ObjectMapper objectMapper = new ObjectMapper();
            RoomPriceUpdateDto priceUpdateDto = objectMapper.readValue(request.getRequestData(), RoomPriceUpdateDto.class);
            HouseholdRoomType householdRoomType = householdRoomTypeRepository.findById(priceUpdateDto.getHouseholdRoomTypeId()).orElseThrow(() -> new ResourceNotFoundException("household.room.type.not.found"));
            if (requestUpdateStatusDto.getRequestStatus().equals(RequestStatus.APPROVED)) {
                householdRoomType.setPrice(priceUpdateDto.getPriceUpdate());
                householdRoomType.setPriceUpdate(priceUpdateDto.getPriceUpdate());
            } else {
                householdRoomType.setPriceUpdate(householdRoomType.getPrice());
            }
            if (householdRoomType.getPrice().compareTo(BigDecimal.valueOf(0)) != 0) {
                householdRoomType.setStatus(HouseholdTypeRoomStatus.ACTIVE);
            }
            householdRoomTypeRepository.save(householdRoomType);
            //update request
            request.setRequestStatus(requestUpdateStatusDto.getRequestStatus());
            request.setRequestResponse(requestUpdateStatusDto.getRequestResponse());
            request.setAdmin(admin);
            request.setSolvedDate(LocalDateTime.now());
            requestRepository.save(request);

            //Send notification
            NotificationRequestPriceRequest notificationRequestPriceRequest = new NotificationRequestPriceRequest();
            notificationRequestPriceRequest.setTitle("Yêu cầu của bạn đã được xử lý");
            notificationRequestPriceRequest.setHouseholdRoomTypeName(priceUpdateDto.getRoomTypeName());
            notificationRequestPriceRequest.setRequestStatus(requestUpdateStatusDto.getRequestStatus());
            notificationService.pushRequestResultNotification(householdRoomType.getHousehold().getManager().getId(), notificationRequestPriceRequest);

        } catch (JsonProcessingException jpe) {
            throw new ResourceBadRequestException("request.data.error");
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("request.update.status.error");
        }
    }

    @Override
    public List<RequestForManagerDto> getRequestByManager() {
        String managerEmail = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found"));
        List<RequestForManagerDto> requestList =  requestRepository.getRequestByManager(managerEmail);
        requestList.forEach(request -> request.setCreatedDate(request.getCreatedDate().plusHours(DateTimeConstant.HOURS_DEPLOY)));
        return requestList;
    }

    @Override
    public void deleteRequestByManager(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .filter(r -> r.getRequestStatus().equals(RequestStatus.PENDING))
                .orElseThrow(() -> new ResourceNotFoundException("request.not.found"));
        try {
            //delete request
            request.setRequestStatus(RequestStatus.DELETED);
            requestRepository.save(request);
            // update priceUpdate
            ObjectMapper objectMapper = new ObjectMapper();
            RoomPriceUpdateDto priceUpdateDto = objectMapper.readValue(request.getRequestData(), RoomPriceUpdateDto.class);
            HouseholdRoomType householdRoomType = householdRoomTypeRepository.findById(priceUpdateDto.getHouseholdRoomTypeId()).orElseThrow(() -> new ResourceNotFoundException("household.room.type.not.found"));
            householdRoomType.setPriceUpdate(householdRoomType.getPrice());
            if (householdRoomType.getPrice().compareTo(BigDecimal.valueOf(0)) != 0) {
                householdRoomType.setStatus(HouseholdTypeRoomStatus.ACTIVE);
            }
            householdRoomTypeRepository.save(householdRoomType);
        } catch (JsonProcessingException jpe) {
            throw new ResourceBadRequestException("request.data.error");
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("request.delete.error");
        }
    }

}

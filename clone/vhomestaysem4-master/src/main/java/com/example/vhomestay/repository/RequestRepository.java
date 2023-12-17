package com.example.vhomestay.repository;

import com.example.vhomestay.enums.RequestStatus;
import com.example.vhomestay.model.dto.response.dashboard.admin.RequestDetailForAdmin;
import com.example.vhomestay.model.dto.response.request.RequestDetailResponseDto;
import com.example.vhomestay.model.dto.response.request.RequestForManagerDto;
import com.example.vhomestay.model.dto.response.request.RequestResponseDto;
import com.example.vhomestay.model.entity.Request;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends BaseRepository<Request, Long> {
    @Query("SELECT new com.example.vhomestay.model.dto.response.request.RequestResponseDto(r.id, h.householdName, h.avatar, r.createdDate, r.requestTitle, r.requestContent, r.solvedDate, '', '', r.requestStatus) " +
            "FROM Request r JOIN  r.household h WHERE r.requestStatus = 'PENDING' ORDER BY r.createdDate DESC")
    List<RequestResponseDto> getRequestPendingByAdmin();
    @Query("SELECT new com.example.vhomestay.model.dto.response.request.RequestResponseDto(r.id, h.householdName, h.avatar, r.createdDate, r.requestTitle, r.requestContent, r.solvedDate, a.firstName, a.lastName, r.requestStatus) " +
            "FROM Request r JOIN  r.household h JOIN r.admin a WHERE r.requestStatus != 'DELETED' AND r.requestStatus != 'PENDING' ORDER BY r.createdDate DESC")
    List<RequestResponseDto> getRequestSolvedByAdmin();

    @Query("SELECT new com.example.vhomestay.model.dto.response.request.RequestForManagerDto(r.id, r.createdDate, r.requestTitle, r.requestContent, r.solvedDate, r.requestResponse, r.requestStatus) " +
            "FROM Request r JOIN r.household h JOIN h.manager m JOIN m.account a " +
            "WHERE a.email = :managerEmail AND r.requestStatus != 'DELETED' ORDER BY r.createdDate DESC")
    List<RequestForManagerDto> getRequestByManager(String managerEmail);

    @Query("SELECT r.requestStatus FROM Request r WHERE r.id = :requestId")
    RequestStatus getRequestStatusByAdmin(Long requestId);

    @Query("SELECT new com.example.vhomestay.model.dto.response.request.RequestDetailResponseDto(h.householdName, h.avatar, m.firstName, m.lastName, m.phoneNumber, ma.email, r.createdDate, r.requestTitle, r.requestContent, r.solvedDate, '', '', r.requestResponse, r.requestStatus) " +
            "FROM Request r JOIN  r.household h JOIN h.manager m JOIN m.account ma WHERE r.id = :requestId AND r.requestStatus = 'PENDING'")
    RequestDetailResponseDto getRequestDetailPendingByAdmin(Long requestId);

    @Query("SELECT new com.example.vhomestay.model.dto.response.request.RequestDetailResponseDto(h.householdName, h.avatar, m.firstName, m.lastName, m.phoneNumber, ma.email, r.createdDate, r.requestTitle, r.requestContent, r.solvedDate, a.firstName, a.lastName, r.requestResponse, r.requestStatus) " +
            "FROM Request r JOIN  r.household h JOIN r.admin a JOIN h.manager m JOIN m.account ma WHERE r.id = :requestId AND r.requestStatus != 'DELETED' AND r.requestStatus != 'PENDING'")
    RequestDetailResponseDto getRequestDetailSolvedByAdmin(Long requestId);
    @Query("SELECT new com.example.vhomestay.model.dto.response.dashboard.admin.RequestDetailForAdmin(" +
            "r.id, h.householdName, r.createdDate, r.requestTitle) " +
            "FROM Request r JOIN r.household h " +
            "where r.requestStatus = 'PENDING' ORDER BY r.createdDate LIMIT 5")
    List<RequestDetailForAdmin> getAllRequestForAdmin();
}

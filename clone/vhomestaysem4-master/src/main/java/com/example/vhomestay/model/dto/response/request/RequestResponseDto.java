package com.example.vhomestay.model.dto.response.request;

import com.example.vhomestay.enums.RequestStatus;
import com.example.vhomestay.model.entity.Admin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestResponseDto {
    private Long requestId;
    private String householdName;
    private String avatarHousehold;
    private LocalDateTime createdDate;
    private String requestTitle;
    private String requestContent;
    private LocalDateTime solvedDate;
    private String adminFirstName;
    private String adminLastName;
    private RequestStatus status;
}

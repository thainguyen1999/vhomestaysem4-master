package com.example.vhomestay.model.dto.response.request;

import com.example.vhomestay.enums.RequestStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestDetailResponseDto {
    private String householdName;
    private String avatarHousehold;
    private String managerFirstName;
    private String managerLastName;
    private String managerPhone;
    private String managerEmail;
    private LocalDateTime createdDate;
    private String requestTitle;
    private String requestContent;
    private LocalDateTime solvedDate;
    private String adminFirstName;
    private String adminLastName;
    private String requestResponse;
    private RequestStatus status;
}

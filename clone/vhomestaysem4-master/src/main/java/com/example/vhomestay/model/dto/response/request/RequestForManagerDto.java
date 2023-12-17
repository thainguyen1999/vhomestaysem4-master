package com.example.vhomestay.model.dto.response.request;

import com.example.vhomestay.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestForManagerDto {
    private Long requestId;
    private LocalDateTime createdDate;
    private String requestTitle;
    private String requestContent;
    private LocalDateTime solvedDate;
    private String requestResponse;
    private RequestStatus status;
}

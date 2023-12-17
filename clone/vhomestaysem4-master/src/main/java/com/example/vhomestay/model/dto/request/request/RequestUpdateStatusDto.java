package com.example.vhomestay.model.dto.request.request;

import com.example.vhomestay.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestUpdateStatusDto {
    private Long requestId;
    private String requestResponse;
    private RequestStatus requestStatus;
}

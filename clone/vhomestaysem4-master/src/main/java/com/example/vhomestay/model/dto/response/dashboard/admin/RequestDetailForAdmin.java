package com.example.vhomestay.model.dto.response.dashboard.admin;

import com.example.vhomestay.enums.RequestType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RequestDetailForAdmin {
    private Long requestId;
    private String householdName;
    private LocalDateTime createdDate;
    private String requestTitle;

    public RequestDetailForAdmin(Long requestId, String householdName, LocalDateTime createdDate, String requestTitle) {
        this.requestId = requestId;
        this.householdName = householdName;
        this.createdDate = createdDate;
        this.requestTitle = requestTitle;
    }
}

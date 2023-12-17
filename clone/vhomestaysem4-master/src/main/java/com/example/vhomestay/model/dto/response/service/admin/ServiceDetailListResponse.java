package com.example.vhomestay.model.dto.response.service.admin;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ServiceDetailListResponse {
    List<ServiceDetailResponse> serviceDetailList;
}

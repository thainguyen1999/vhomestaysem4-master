package com.example.vhomestay.model.dto.response.service.customer;

import com.example.vhomestay.model.dto.response.service.manager.ServiceDetailResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ServiceDetailListResponse {
    List<ServiceDetailResponse> serviceDetailListForManager;
}

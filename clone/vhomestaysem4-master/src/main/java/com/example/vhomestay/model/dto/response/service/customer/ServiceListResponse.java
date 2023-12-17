package com.example.vhomestay.model.dto.response.service.customer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ServiceListResponse {
    List<ServiceResponse> serviceListForCustomer;
}

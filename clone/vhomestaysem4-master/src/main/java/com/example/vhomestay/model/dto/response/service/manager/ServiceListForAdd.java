package com.example.vhomestay.model.dto.response.service.manager;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ServiceListForAdd {
    private List<ServiceDetailForAddResponse> serviceListForAdd;
}

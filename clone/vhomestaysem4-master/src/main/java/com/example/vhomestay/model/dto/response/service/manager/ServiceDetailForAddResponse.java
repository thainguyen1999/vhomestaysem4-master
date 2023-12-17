package com.example.vhomestay.model.dto.response.service.manager;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ServiceDetailForAddResponse {
    private Long serviceId;
    private String serviceName;

    public ServiceDetailForAddResponse(Long serviceId, String serviceName) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
    }
}

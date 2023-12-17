package com.example.vhomestay.model.dto.response.service.customer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ServiceResponse {
    private Long serviceId;
    private String serviceName;
    private String serviceAvatar;

    public ServiceResponse(Long serviceId, String serviceName, String serviceAvatar) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceAvatar = serviceAvatar;
    }
}

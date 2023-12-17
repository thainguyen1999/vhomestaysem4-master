package com.example.vhomestay.model.dto.response.service.admin;

import com.example.vhomestay.enums.ServiceStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ServiceDetailResponse {
    private Long serviceId;
    private String serviceName;
    private String serviceAvatar;
    private String serviceDescription;
    private ServiceStatus serviceStatus;

    public ServiceDetailResponse(Long serviceId, String serviceName, String serviceAvatar, String serviceDescription, ServiceStatus serviceStatus) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceAvatar = serviceAvatar;
        this.serviceDescription = serviceDescription;
        this.serviceStatus = serviceStatus;
    }
}

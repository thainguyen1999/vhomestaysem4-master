package com.example.vhomestay.model.dto.response.service.manager;

import com.example.vhomestay.enums.ServiceStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ServiceDetailResponse {
    private Long serviceId;
    private Long householdServiceId;
    private String serviceName;
    private String serviceAvatar;
    private String serviceDescription;
    private ServiceStatus serviceStatus;

    public ServiceDetailResponse(Long serviceId, Long householdServiceId, String serviceName, String serviceAvatar, String serviceDescription, ServiceStatus serviceStatus) {
        this.serviceId = serviceId;
        this.householdServiceId = householdServiceId;
        this.serviceName = serviceName;
        this.serviceAvatar = serviceAvatar;
        this.serviceDescription = serviceDescription;
        this.serviceStatus = serviceStatus;
    }
}

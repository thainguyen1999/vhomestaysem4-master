package com.example.vhomestay.model.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ServiceRequest {
    private Long serviceId;
    @Nationalized
    private String serviceName;
    @Nationalized
    private String serviceDescription;
    private MultipartFile imageFile;
}

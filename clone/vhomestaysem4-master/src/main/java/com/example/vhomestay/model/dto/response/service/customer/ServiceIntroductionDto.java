package com.example.vhomestay.model.dto.response.service.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceIntroductionDto {
    private String serviceName;
    private String description;
    private String image;
}

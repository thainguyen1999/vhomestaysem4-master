package com.example.vhomestay.model.dto.response.booking.customer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HouseholdServiceDto {
    private Long householdServiceId;
    private Long serviceId;
    private String imageUri;
    private String serviceName;
    private String description;

}

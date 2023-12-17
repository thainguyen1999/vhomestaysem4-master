package com.example.vhomestay.model.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
public class AddHouseholdServiceRequest {
    private Long serviceId;
    @Nationalized
    private String serviceDescription;
}

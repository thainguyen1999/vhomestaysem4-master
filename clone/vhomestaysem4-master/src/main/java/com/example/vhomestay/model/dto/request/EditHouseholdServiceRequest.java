package com.example.vhomestay.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditHouseholdServiceRequest {
    private Long householdServiceId;
    private String serviceDescription;
}

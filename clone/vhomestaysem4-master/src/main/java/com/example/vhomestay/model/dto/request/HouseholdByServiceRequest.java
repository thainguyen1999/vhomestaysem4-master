package com.example.vhomestay.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class HouseholdByServiceRequest {
    private List<Long> serviceIdList;
}

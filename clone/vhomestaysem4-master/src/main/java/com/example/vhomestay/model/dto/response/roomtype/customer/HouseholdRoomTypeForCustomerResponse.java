package com.example.vhomestay.model.dto.response.roomtype.customer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HouseholdRoomTypeForCustomerResponse {
    private String roomTypeName;
    private Integer capacity;
    private Integer singleBed;
    private Integer doubleBed;

    public HouseholdRoomTypeForCustomerResponse(String roomTypeName, Integer capacity, Integer singleBed, Integer doubleBed) {
        this.roomTypeName = roomTypeName;
        this.capacity = capacity;
        this.singleBed = singleBed;
        this.doubleBed = doubleBed;
    }
}

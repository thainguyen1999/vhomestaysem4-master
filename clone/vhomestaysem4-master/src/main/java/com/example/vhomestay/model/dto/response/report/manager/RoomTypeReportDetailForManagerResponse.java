package com.example.vhomestay.model.dto.response.report.manager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeReportDetailForManagerResponse {
    private String homestayCode;
    private String roomTypeName;
    private Integer totalRoom;
    private Integer totalDormSlot;
    private Integer totalCustomer;
    private Integer totalCustomerByDay;
    private BigDecimal totalRevenue;

    public RoomTypeReportDetailForManagerResponse(Integer totalRoom, Integer totalDormSlot, Integer totalCustomer, Integer totalCustomerByDay, BigDecimal totalRevenue) {
        this.totalRoom = totalRoom;
        this.totalDormSlot = totalDormSlot;
        this.totalCustomer = totalCustomer;
        this.totalCustomerByDay = totalCustomerByDay;
        this.totalRevenue = totalRevenue;
    }
}

package com.example.vhomestay.model.dto.response.report.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportDetailForAdminResponse {
    private Long householdId;
    private String householdName;
    private String managerName;
    private List<String> homeStayName;
    private Integer totalHomestay;
    private Integer totalCapacity;
    private Integer totalCustomer;
    private Integer totalCustomerByDay;
    private BigDecimal totalRevenue;
}

package com.example.vhomestay.model.dto.response.report.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportHouseholdPayment {
    private String householdName;
    private String managerName;
    private String managerEmail;
    private String managerPhone;
    private BigDecimal totalRevenue;
}

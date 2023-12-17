package com.example.vhomestay.model.dto.response.dashboard.admin;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardForAdmin {
    private Integer totalArea;
    private Integer totalHousehold;
    private Integer totalHomestay;
    private Integer totalRoomType;
    private Integer totalUser;
    private Integer totalService;
    private Integer totalLocalProduct;
    private Integer totalNews;
    List<RequestDetailForAdmin> requestDetailListForAdmin;
    private String session;
    private String temperature;
    private String weather;
    private List<Integer> getTotalGuestByMonthForThisYear;
    private List<Integer> getTotalGuestByMonthForLastYear;
}

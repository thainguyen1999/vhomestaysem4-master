package com.example.vhomestay.model.dto.response.dashboard.admin;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListTotalGuestForAdmin {
    List<Integer> totalGuestListForAdmin;
}

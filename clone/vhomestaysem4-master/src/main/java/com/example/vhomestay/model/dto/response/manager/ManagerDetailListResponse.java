package com.example.vhomestay.model.dto.response.manager;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerDetailListResponse {
    private List<ManagerDetailResponse> listManagerDetailForAdmin;
}

package com.example.vhomestay.model.dto.response.manager;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ManagerDetailResponse {
    private Long managerId;
    private String firstName;
    private String lastName;

    public ManagerDetailResponse(Long managerId, String firstName, String lastName) {
        this.managerId = managerId;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}

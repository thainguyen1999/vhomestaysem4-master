package com.example.vhomestay.model.dto.response.user;

import com.example.vhomestay.enums.AccountRole;
import com.example.vhomestay.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserInfoResponseDto {
    private Long accountId;
    private String avatar;
    private String email;
    private String firstName;
    private String lastName;
    private AccountRole role;
    private LocalDateTime createdDate;
    private String houseHoldName;
    private AccountStatus status;

}

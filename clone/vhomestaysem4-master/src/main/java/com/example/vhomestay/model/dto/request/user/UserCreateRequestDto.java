package com.example.vhomestay.model.dto.request.user;

import com.example.vhomestay.enums.AccountRole;
import lombok.Data;

@Data
public class UserCreateRequestDto {
    private AccountRole role;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Long householdId;
}

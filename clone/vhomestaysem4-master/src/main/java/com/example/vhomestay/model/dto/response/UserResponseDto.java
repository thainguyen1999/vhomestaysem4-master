package com.example.vhomestay.model.dto.response;

import com.example.vhomestay.enums.AccountRole;
import com.example.vhomestay.enums.Gender;
import com.example.vhomestay.enums.Provider;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String avatar;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String address;
    private Long accountId;
    private AccountRole role;
    private Provider provider;
}

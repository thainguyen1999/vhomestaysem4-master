package com.example.vhomestay.model.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordForResetRequestDto {
    private String email;
    @NotBlank
    private String newPassword;
    @NotBlank
    private String confirmPassword;
}

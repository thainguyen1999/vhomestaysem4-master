package com.example.vhomestay.model.dto.request;

import com.example.vhomestay.enums.Provider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialLoginRequestDto {
    @NotNull
    private String email;
    @NotBlank
    private String fullName;
    private String avatar;
    private Provider provider;
    private String providerId;
    private String accessToken;
    private String refreshToken;
}

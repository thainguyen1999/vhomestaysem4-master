package com.example.vhomestay.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.vhomestay.model.entity.Customer;
import com.example.vhomestay.model.entity.Manager;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("user")
    private UserResponseDto user;
}

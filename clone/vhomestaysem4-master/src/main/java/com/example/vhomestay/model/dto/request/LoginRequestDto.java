package com.example.vhomestay.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class LoginRequestDto {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}

package com.example.vhomestay.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public class HomestayRequestDto {
    @NotBlank
    private String homestayCode;
    @NotBlank
    private String address;
}

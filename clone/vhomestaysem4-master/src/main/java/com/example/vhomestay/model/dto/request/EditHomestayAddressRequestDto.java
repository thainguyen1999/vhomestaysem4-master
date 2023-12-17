package com.example.vhomestay.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EditHomestayAddressRequestDto {
    @NotBlank
    private String newAddress;
}

package com.example.vhomestay.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditHomestayCodeRequestDto {
    @NotBlank
    private String newHomestayCode;
}

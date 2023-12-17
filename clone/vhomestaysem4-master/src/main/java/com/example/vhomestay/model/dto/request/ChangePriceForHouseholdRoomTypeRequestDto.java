package com.example.vhomestay.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ChangePriceForHouseholdRoomTypeRequestDto {
    @NotBlank
    private BigDecimal price;
}

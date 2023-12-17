package com.example.vhomestay.model.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class HomestayDto {
    private Long id;
    private String homestayCode;
    private String homestayAddress;

    public HomestayDto(Long id, String homestayCode, String homestayAddress) {
        this.id = id;
        this.homestayCode = homestayCode;
        this.homestayAddress = homestayAddress;
    }
}

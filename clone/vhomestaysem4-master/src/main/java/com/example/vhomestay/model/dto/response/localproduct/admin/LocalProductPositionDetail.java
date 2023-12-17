package com.example.vhomestay.model.dto.response.localproduct.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LocalProductPositionDetail {
    private Long localProductId;
    private String localProductName;

    public LocalProductPositionDetail(Long localProductId, String localProductName) {
        this.localProductId = localProductId;
        this.localProductName = localProductName;
    }
}

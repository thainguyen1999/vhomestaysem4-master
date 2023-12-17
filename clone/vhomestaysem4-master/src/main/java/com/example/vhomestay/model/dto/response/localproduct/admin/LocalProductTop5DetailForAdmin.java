package com.example.vhomestay.model.dto.response.localproduct.admin;

import com.example.vhomestay.enums.LocalProductPosition;
import com.example.vhomestay.enums.LocalProductType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LocalProductTop5DetailForAdmin {
    private LocalProductPosition localProductPosition;
    private Long localProductId;
    private String localProductName;
    private LocalProductType type;

    public LocalProductTop5DetailForAdmin(LocalProductPosition localProductPosition, Long localProductId, String localProductName, LocalProductType type) {
        this.localProductPosition = localProductPosition;
        this.localProductId = localProductId;
        this.localProductName = localProductName;
        this.type = type;
    }
}

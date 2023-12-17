package com.example.vhomestay.model.dto.request.localproduct;

import com.example.vhomestay.enums.LocalProductPosition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalProductPositionRequest {
    private Long localProductId;
    private LocalProductPosition localProductPosition;
}

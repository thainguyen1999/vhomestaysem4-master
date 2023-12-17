package com.example.vhomestay.model.dto.response.localproduct.admin;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddLocalProductResponse {
    List<LocalProductPositionDetail> localProductPositionListForAdmin;
    List<String> localProductNullPositionListForAdmin;
}

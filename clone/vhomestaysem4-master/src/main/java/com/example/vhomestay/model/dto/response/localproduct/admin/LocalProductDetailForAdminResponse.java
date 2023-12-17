package com.example.vhomestay.model.dto.response.localproduct.admin;

import com.example.vhomestay.enums.BaseStatus;
import com.example.vhomestay.enums.LocalProductType;
import com.example.vhomestay.model.entity.VillageMedia;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LocalProductDetailForAdminResponse {
    private Long id;
    private String productName;
    private String productDescription;
    private String unit;
    private BaseStatus status;
    private LocalProductType type;
    private BigDecimal lowestPrice;
    private BigDecimal highestPrice;
    private List<VillageMedia> villageMedias;

    public LocalProductDetailForAdminResponse(Long id, String productName, String productDescription, String unit, BaseStatus status, LocalProductType type, BigDecimal lowestPrice, BigDecimal highestPrice) {
        this.id = id;
        this.productName = productName;
        this.productDescription = productDescription;
        this.unit = unit;
        this.status = status;
        this.type = type;
        this.lowestPrice = lowestPrice;
        this.highestPrice = highestPrice;
    }
}

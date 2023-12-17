package com.example.vhomestay.model.dto.response.localproduct.customer;

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
public class LocalProductDetailForCustomerResponse {
    private Long id;
    private String productName;
    private String productDescription;
    private String unit;
    private LocalProductType type;
    private BigDecimal lowestPrice;
    private BigDecimal highestPrice;
    private List<VillageMedia> villageMedias;

    public LocalProductDetailForCustomerResponse(Long id, String productName, String productDescription, String unit, LocalProductType type, BigDecimal lowestPrice, BigDecimal highestPrice) {
        this.id = id;
        this.productName = productName;
        this.productDescription = productDescription;
        this.unit = unit;
        this.type = type;
        this.lowestPrice = lowestPrice;
        this.highestPrice = highestPrice;
    }
}

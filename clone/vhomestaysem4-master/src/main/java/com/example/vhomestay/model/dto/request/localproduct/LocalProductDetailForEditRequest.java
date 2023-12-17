package com.example.vhomestay.model.dto.request.localproduct;

import com.example.vhomestay.enums.LocalProductType;
import com.example.vhomestay.model.entity.VillageMedia;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class LocalProductDetailForEditRequest {
    private Long localProductId;
    private String localProductName;
    private LocalProductType localProductType;
    private String localProductDescription;
    private String localProductUnit;
    private BigDecimal localProductMinPrice;
    private BigDecimal localProductMaxPrice;
    private List<VillageMedia> villageMedias;
    private List<MultipartFile> imagesFile;
}

package com.example.vhomestay.model.dto.response.homestay;

import com.example.vhomestay.model.entity.HomestayMedia;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class HomestayDetailForCustomerResponse {
    private Long homestayId;
    private String homestayCode;
    @Nationalized
    private String fullAddress;
    private List<HomestayMedia> medias;

    public HomestayDetailForCustomerResponse(Long homestayId, String homestayCode, String fullAddress) {
        this.homestayId = homestayId;
        this.homestayCode = homestayCode;
        this.fullAddress = fullAddress;
    }
}

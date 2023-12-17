package com.example.vhomestay.model.dto.response.localproduct.customer;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListLocalProductForCustomer {
    List<LocalProductDetailForCustomerResponse> localProductListForCustomer;
}

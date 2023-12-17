package com.example.vhomestay.controller.customer;

import com.example.vhomestay.model.dto.response.localproduct.customer.ListLocalProductForCustomer;
import com.example.vhomestay.model.dto.response.localproduct.customer.LocalProductDetailForCustomerResponse;
import com.example.vhomestay.service.LocalProductService;
import com.example.vhomestay.service.impl.LocalProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/local-product")
@RequiredArgsConstructor
public class LocalProductCustomerController {
    private final LocalProductService localProductService;
    @GetMapping
    ListLocalProductForCustomer getAllLocalProductForCustomer() {
        List<LocalProductDetailForCustomerResponse>  localProductListForCustomer = localProductService.findAllLocalProductForCustomer();
        return ListLocalProductForCustomer.builder().localProductListForCustomer(localProductListForCustomer).build();
    }

    @GetMapping("/{localProductId}")
    LocalProductDetailForCustomerResponse getLocalProductByIdForCustomer(@PathVariable Long localProductId) {
        return localProductService.findLocalProductByIdForCustomer(localProductId);
    }
}

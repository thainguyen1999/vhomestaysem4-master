package com.example.vhomestay.controller.customer;

import com.example.vhomestay.model.dto.request.HouseholdDetailRequest;
import com.example.vhomestay.model.dto.response.household.customer.HouseholdDetailForCustomer;
import com.example.vhomestay.service.HouseholdService;
import com.example.vhomestay.service.impl.HouseholdServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer/household")
@RequiredArgsConstructor
public class HouseholdCustomerController {

    private final HouseholdService householdService;

    @GetMapping("{householdId}")
    public HouseholdDetailForCustomer getHouseholdForCustomerById(@PathVariable Long householdId) {
        HouseholdDetailForCustomer householdDetailForCustomer = householdService.getHouseholdDetailForCustomer(householdId);
        if (householdDetailForCustomer != null)
            return householdDetailForCustomer;
        else
            throw new RuntimeException("manager.household.notfound");
    }
}

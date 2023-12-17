package com.example.vhomestay.service;

import com.example.vhomestay.model.entity.CustomerBankInformation;

public interface CustomerBankInformationService {
    void saveCustomerBankInformation(CustomerBankInformation customerBankInformation);
    CustomerBankInformation findByCustomerId(Long id);
}

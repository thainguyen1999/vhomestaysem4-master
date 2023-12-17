package com.example.vhomestay.service.impl;

import com.example.vhomestay.model.entity.CustomerBankInformation;
import com.example.vhomestay.repository.CustomerBankInformationRepository;
import com.example.vhomestay.service.CustomerBankInformationService;
import com.example.vhomestay.util.exception.ResourceInternalServerErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerBankInformationServiceImpl extends BaseServiceImpl<CustomerBankInformation, Long, CustomerBankInformationRepository>
        implements CustomerBankInformationService {
    private final CustomerBankInformationRepository customerBankInformationRepository;

    @Override
    public void saveCustomerBankInformation(CustomerBankInformation customerBankInformation) {
        try {
            customerBankInformationRepository.save(customerBankInformation);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public CustomerBankInformation findByCustomerId(Long id) {
        try {
            Optional<CustomerBankInformation> customerBankInformation = customerBankInformationRepository.findByCustomerId(id);
            return customerBankInformation.orElse(null);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }
}

package com.example.vhomestay.service.impl;

import com.example.vhomestay.model.dto.response.service.customer.HouseholdByServiceResponse;
import com.example.vhomestay.model.dto.response.service.customer.ServiceDetailResponse;
import com.example.vhomestay.model.dto.response.service.customer.ServiceIntroductionDto;
import com.example.vhomestay.model.dto.response.service.customer.ServiceResponse;
import com.example.vhomestay.repository.ServiceRepository;
import com.example.vhomestay.service.ServiceForCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceForCustomerServiceImpl implements ServiceForCustomerService {

    private final ServiceRepository serviceRepository;

    @Override
    public List<ServiceResponse> getAllService() {
        return serviceRepository.getAllService();
    }

    @Override
    public List<HouseholdByServiceResponse> getHouseholdByService(List<Long> serviceIdList) {
        return serviceRepository.getHouseholdByServiceId(serviceIdList, serviceIdList.size());
    }

    @Override
    public ServiceDetailResponse getServiceById(Long serviceId) {
        return serviceRepository.getServiceById(serviceId);
    }

    @Override
    public List<ServiceIntroductionDto> getAllServiceInHomePage() {
        return serviceRepository.getAllServiceInHomePage();
    }
}

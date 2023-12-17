package com.example.vhomestay.service;

import com.example.vhomestay.model.dto.response.service.customer.HouseholdByServiceResponse;
import com.example.vhomestay.model.dto.response.service.customer.ServiceDetailResponse;
import com.example.vhomestay.model.dto.response.service.customer.ServiceIntroductionDto;
import com.example.vhomestay.model.dto.response.service.customer.ServiceResponse;

import java.util.List;

public interface ServiceForCustomerService {
    List<ServiceResponse> getAllService();
    List<HouseholdByServiceResponse> getHouseholdByService(List<Long> serviceIdList);
    ServiceDetailResponse getServiceById(Long serviceId);
    List<ServiceIntroductionDto> getAllServiceInHomePage();
}

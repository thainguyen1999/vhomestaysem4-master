package com.example.vhomestay.service;

import com.example.vhomestay.model.dto.request.AddHouseholdServiceRequest;
import com.example.vhomestay.model.dto.request.EditHouseholdServiceRequest;
import com.example.vhomestay.model.dto.response.service.customer.ServiceResponse;
import com.example.vhomestay.model.dto.response.service.manager.ServiceDetailForAddResponse;
import com.example.vhomestay.model.dto.response.service.manager.ServiceDetailResponse;

import java.util.List;

public interface ManageServiceForManagerService {
    List<ServiceDetailResponse> getAllService(String managerEmail);
    List<ServiceDetailForAddResponse> getAllServiceForAdd();
    List<ServiceResponse> getAllServiceAvailable(String managerEmail);
    boolean createService(AddHouseholdServiceRequest addHouseholdServiceRequest, String managerEmail);
    boolean editService(EditHouseholdServiceRequest editHouseholdServiceRequest);
    boolean activeService(Long householdServiceId);
    boolean inactiveService(Long householdServiceId);
    boolean deleteService(Long householdServiceId);
}

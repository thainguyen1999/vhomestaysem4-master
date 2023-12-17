package com.example.vhomestay.controller.manager;

import com.example.vhomestay.model.dto.request.AddHouseholdServiceRequest;
import com.example.vhomestay.model.dto.request.EditHouseholdServiceRequest;
import com.example.vhomestay.model.dto.response.MessageResponseDto;
import com.example.vhomestay.model.dto.response.service.manager.ServiceDetailForAddResponse;
import com.example.vhomestay.model.dto.response.service.manager.ServiceDetailListResponse;
import com.example.vhomestay.model.dto.response.service.customer.ServiceResponse;
import com.example.vhomestay.model.dto.response.service.manager.ServiceDetailResponse;
import com.example.vhomestay.security.SecurityUtil;
import com.example.vhomestay.service.ManageServiceForManagerService;
import com.example.vhomestay.service.impl.HouseholdServiceImpl;
import com.example.vhomestay.service.impl.ManageServiceForManagerServiceImpl;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/manager/service")
@RequiredArgsConstructor
public class ManageServiceManagerController {

    private final ManageServiceForManagerService serviceForManagerService;

    @GetMapping
    public ServiceDetailListResponse getAllService() {
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        String managerEmail = currentUserLoginEmailOptional.get();
        List<ServiceDetailResponse> serviceDetailList = serviceForManagerService.getAllService(managerEmail);
        List<ServiceDetailForAddResponse> serviceListForAdd = serviceForManagerService.getAllServiceForAdd();
        return ServiceDetailListResponse.builder()
                .serviceDetailList(serviceDetailList)
                .serviceListForAdd(serviceListForAdd)
                .build();
    }

    @GetMapping("/add")
    public ResponseEntity<?> addService() {
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        String managerEmail = currentUserLoginEmailOptional.get();
        List<ServiceResponse> serviceResponseList = serviceForManagerService.getAllServiceAvailable(managerEmail);
        Map<String, Object> response = Map.of("serviceList", serviceResponseList);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addService(@RequestBody AddHouseholdServiceRequest addHouseholdServiceRequest) {
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        String managerEmail = currentUserLoginEmailOptional.get();
        if (serviceForManagerService.createService(addHouseholdServiceRequest, managerEmail)){
            MessageResponseDto messageResponseDto = new MessageResponseDto("service.add.success", HttpStatus.OK);
            return ResponseEntity.ok(messageResponseDto);
        } else {
            throw new ResourceBadRequestException("service.add.failed");
        }
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editService(@RequestBody EditHouseholdServiceRequest editHouseholdServiceRequest) {
        if (serviceForManagerService.editService(editHouseholdServiceRequest)){
            MessageResponseDto messageResponseDto = new MessageResponseDto("service.update.success", HttpStatus.OK);
            return ResponseEntity.ok(messageResponseDto);
        } else {
            throw new ResourceBadRequestException("service.update.failed");
        }
    }

    @PostMapping("/{householdServiceId}/active")
    public ResponseEntity<?> activeService(@PathVariable Long householdServiceId) {
        if (serviceForManagerService.activeService(householdServiceId)){
            MessageResponseDto messageResponseDto = new MessageResponseDto("service.active.success", HttpStatus.OK);
            return ResponseEntity.ok(messageResponseDto);
        } else {
            throw new ResourceBadRequestException("service.not.found.or.user.not.have.permission");
        }
    }

    @PostMapping("/{householdServiceId}/deactive")
    public ResponseEntity<?> inactiveService(@PathVariable Long householdServiceId) {
        if (serviceForManagerService.inactiveService(householdServiceId)){
            return ResponseEntity.ok("service.inactive.failed");
        } else {
            throw new ResourceBadRequestException("service.not.found.or.user.not.have.permission");
        }
    }

    @PostMapping("/{householdServiceId}/delete")
        public ResponseEntity<?> deleteService(@PathVariable Long householdServiceId) {
        if (serviceForManagerService.deleteService(householdServiceId)){
            return ResponseEntity.ok("service.delete.success");
        } else {
            throw new ResourceBadRequestException("service.inactive.failed");
        }
    }
}

package com.example.vhomestay.controller.admin;

import com.example.vhomestay.model.dto.request.ServiceRequest;
import com.example.vhomestay.model.dto.response.MessageResponseDto;
import com.example.vhomestay.model.dto.response.service.admin.ServiceDetailListResponse;
import com.example.vhomestay.service.ManageServiceForAdminService;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/admin/service")
@RequiredArgsConstructor
public class ManageServiceAdminController {

    private final ManageServiceForAdminService serviceForManagerService;
    private final MessageSource messageSource;

    @GetMapping
    public ServiceDetailListResponse getAllService() {
        return ServiceDetailListResponse.builder().serviceDetailList(serviceForManagerService.getAllService()).build();
    }

    @PostMapping("/add")
    public ResponseEntity<?> addService(@ModelAttribute ServiceRequest serviceRequest) throws IOException {
        if (serviceForManagerService.addService(serviceRequest)){
            return ResponseEntity.ok("service.add.success");
        } else {
            throw new ResourceBadRequestException("service.not.found");
        }
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editService(@ModelAttribute ServiceRequest serviceRequest) throws IOException {
        if (serviceForManagerService.editService(serviceRequest)){
            return ResponseEntity.ok("service.update.success");
        } else {
            throw new ResourceBadRequestException("service.not.found");
        }
    }

    @PutMapping("/{serviceId}/active")
    public ResponseEntity<?> activeService(@PathVariable Long serviceId) {
        if (serviceForManagerService.activeService(serviceId)){
            MessageResponseDto messageResponseDto = new MessageResponseDto("Active service successfully.", HttpStatus.OK);
            return ResponseEntity.ok(messageResponseDto);
        } else {
            throw new ResourceBadRequestException(messageSource.getMessage("This doesn't exits", null, Locale.getDefault()));
        }
    }

    @PutMapping("/{serviceId}/inactive")
    public ResponseEntity<?> inactiveService(@PathVariable Long serviceId) {
        if (serviceForManagerService.inactiveService(serviceId)){
            return ResponseEntity.ok("service.inactive.failed");
        } else {
            throw new ResourceBadRequestException(messageSource.getMessage("service.not.found", null, Locale.getDefault()));
        }
    }

    @PutMapping("/{serviceId}/delete")
    public ResponseEntity<?> deleteService(@PathVariable Long serviceId) {
        if (serviceForManagerService.deleteService(serviceId)){
            return ResponseEntity.ok("service.delete.success");
        } else {
            throw new ResourceBadRequestException("service.not.found");
        }
    }


}

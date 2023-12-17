package com.example.vhomestay.controller.admin;

import com.example.vhomestay.model.dto.response.MessageResponseDto;
import com.example.vhomestay.model.dto.response.facility.FacilityAdminResponseDto;
import com.example.vhomestay.model.entity.Facility;
import com.example.vhomestay.repository.FacilityRepository;
import com.example.vhomestay.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/facilities")
@RequiredArgsConstructor
public class FacilityAdminController {
    private final FacilityService facilityService;
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllFacilitiesByAdmin() {
        List<FacilityAdminResponseDto> facilities = facilityService.findAllActive();

        Map<String, Object> response = Map.of(
                "facilities", facilities
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateFacilityByAdmin(@RequestBody FacilityAdminResponseDto facilityAdminResponseDto) {
        facilityService.updateFacilityByAdmin(facilityAdminResponseDto);

        return ResponseEntity.ok("facility.update.success");
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createFacilityByAdmin(@RequestBody FacilityAdminResponseDto facilityAdminResponseDto) {
        facilityService.createFacilityByAdmin(facilityAdminResponseDto);

        return ResponseEntity.ok("facility.add.success");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteFacilityByAdmin(@PathVariable Long id) {
        facilityService.deleteFacilityByAdmin(id);

        return ResponseEntity.ok("facility.delete.success");
    }
}

package com.example.vhomestay.controller.admin;

import com.example.vhomestay.model.dto.request.homestay.HomestayAdminRequestDto;
import com.example.vhomestay.model.dto.response.HomestayInformationForManager;
import com.example.vhomestay.model.dto.response.homestay.admin.HomestayAdminResponseDto;
import com.example.vhomestay.model.dto.response.homestay.admin.HomestayCommonAdminResponseDto;
import com.example.vhomestay.model.dto.response.homestay.admin.HomestayListAdminResponseDto;
import com.example.vhomestay.model.entity.Homestay;
import com.example.vhomestay.service.HomestayService;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin/homestays")
@RequiredArgsConstructor
public class HomestayAdminController {
    private final HomestayService homestayService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getHomestaysByAdmin() {
        List<HomestayListAdminResponseDto> homestayListAdminResponseDtos = homestayService.findAllHomestaysByAdmin();

        Map<String, Object> response = Map.of(
                "homestayListAdminResponseDtos", homestayListAdminResponseDtos
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{homestayId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getHomestayById(@PathVariable Long homestayId) {
        HomestayCommonAdminResponseDto homestayCommonAdminResponseDto =
                homestayService.findHomestayCommonInformationByAdmin(homestayId).orElseThrow(() -> new ResourceNotFoundException("Homestay not found"));

        return ResponseEntity.ok(homestayCommonAdminResponseDto);
    }

    @GetMapping("/form-create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> showAddHomestayFormByAdmin() {
        HomestayAdminResponseDto homestayAdminResponseDto = homestayService.showHomestayFormByAdmin();

        return ResponseEntity.ok(homestayAdminResponseDto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addHomestayByAdmin(@RequestBody HomestayAdminRequestDto homestayAdminRequestDto) {
        homestayService.addHomestayByAdmin(homestayAdminRequestDto);

        return ResponseEntity.ok("homestay.add.success");
    }

    @GetMapping("/{homestayId}/form-update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> showUpdateHomestayFormByAdmin(@PathVariable Long homestayId) {
        HomestayAdminResponseDto homestayAdminResponseDto = homestayService.showHomestayFormByAdmin();
        HomestayAdminRequestDto homestayAdminRequestDto = new HomestayAdminRequestDto();

        Optional<Homestay> homestayOptional = homestayService.findHomestayById(homestayId);
        Homestay homestay = homestayOptional.orElseThrow(() -> new ResourceNotFoundException("homestay.not.found"));

        homestayAdminRequestDto.setHomestayId(homestay.getId());
        homestayAdminRequestDto.setAreaId(homestay.getArea().getId());
        homestayAdminRequestDto.setHouseholdId(homestay.getHousehold().getId());
        homestayAdminRequestDto.setHomestayName(homestay.getHomestayCode());

        Map<String, Object> homestayUpdateInformation = Map.of(
                "homestayAdminResponseDto", homestayAdminResponseDto,
                "homestayAdminRequestDto", homestayAdminRequestDto
        );

        return ResponseEntity.ok(homestayUpdateInformation);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateHomestayByAdmin(@RequestBody HomestayAdminRequestDto homestayAdminRequestDto) {
        homestayService.updateHomestayByAdmin(homestayAdminRequestDto);

        return ResponseEntity.ok("homestay.update.success");
    }
    @DeleteMapping("/{homestayId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteHomestayByAdmin(@PathVariable Long homestayId) {
        homestayService.deleteHomestayByAdmin(homestayId);

        return ResponseEntity.ok("homestay.delete.success");
    }

    @PutMapping("/{homestayId}/hide")
    public ResponseEntity<?> hideHomestayByAdmin(@PathVariable Long homestayId) {
        homestayService.hideOrShowHomestay(homestayId);

        return ResponseEntity.ok("homestay.hide.success");
    }

    @PutMapping("/{homestayId}/show")
    public ResponseEntity<?> showHomestayByAdmin(@PathVariable Long homestayId) {
        homestayService.hideOrShowHomestay(homestayId);

        return ResponseEntity.ok("homestay.show.success");
    }
}

package com.example.vhomestay.controller.admin;

import com.example.vhomestay.model.dto.response.MessageResponseDto;
import com.example.vhomestay.model.dto.response.village.VillageMediaCreateAndUpdateResponse;
import com.example.vhomestay.service.VillageMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/village-media")
@RequiredArgsConstructor
public class VillageMediaController {
    private final VillageMediaService villageMediaService;
    private final MessageSource messageSource;

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN') OR hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAllVillageMedia() {
        VillageMediaCreateAndUpdateResponse response = villageMediaService.getAllVillageMedia();
        return ResponseEntity.ok(response);
    }
    @PutMapping()
    @PreAuthorize("hasRole('ADMIN') OR hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createAndUpdateVillageMedia(@ModelAttribute VillageMediaCreateAndUpdateResponse villageMediaCreateAndUpdateResponse) {
        villageMediaService.createAndUpdateVillageMedia(villageMediaCreateAndUpdateResponse);
        return ResponseEntity.ok("media.create.and.update.success");
    }

}

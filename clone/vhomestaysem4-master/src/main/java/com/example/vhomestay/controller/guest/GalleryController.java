package com.example.vhomestay.controller.guest;

import com.example.vhomestay.service.VillageMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/v1/gallery")
@RequiredArgsConstructor
public class GalleryController {
    private final VillageMediaService villageMediaService;
    @GetMapping()
    public ResponseEntity<?> getAllVillageMedia() {
        List<String> villageMediaList = villageMediaService.getAllUrlVillageMedia();
        return ResponseEntity.ok(villageMediaList);
    }
}

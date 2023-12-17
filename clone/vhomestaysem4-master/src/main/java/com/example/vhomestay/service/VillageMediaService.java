package com.example.vhomestay.service;

import com.example.vhomestay.model.dto.response.village.VillageMediaCreateAndUpdateResponse;
import com.example.vhomestay.model.entity.VillageMedia;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface VillageMediaService extends BaseService<VillageMedia, Long> {
    List<String> getAllUrlVillageMedia();
    VillageMediaCreateAndUpdateResponse getAllVillageMedia();
    @Transactional
    void createAndUpdateVillageMedia(VillageMediaCreateAndUpdateResponse villageMediaCreateAndUpdateResponse);
    List<String> getVillageMediaHomePage();

    List<Map<String, String>> uploadImage(List<MultipartFile> image) throws IOException;

    void uploadImageHomestay(Long homestayId, List<MultipartFile> image) throws IOException;

    void uploadImageForProduct(List<MultipartFile> image) throws IOException;
}

package com.example.vhomestay.service.impl;

import com.example.vhomestay.enums.MediaType;
import com.example.vhomestay.enums.MediaVillagePosition;
import com.example.vhomestay.model.dto.response.village.VillageMediaCreateAndUpdateResponse;
import com.example.vhomestay.model.entity.Homestay;
import com.example.vhomestay.model.entity.HomestayMedia;
import com.example.vhomestay.model.entity.LocalProduct;
import com.example.vhomestay.model.entity.VillageMedia;
import com.example.vhomestay.repository.HomestayMediaRepository;
import com.example.vhomestay.repository.HomestayRepository;
import com.example.vhomestay.repository.LocalProductRepository;
import com.example.vhomestay.repository.VillageMediaRepository;
import com.example.vhomestay.service.StorageService;
import com.example.vhomestay.service.VillageMediaService;
import com.example.vhomestay.util.exception.ResourceInternalServerErrorException;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VillageMediaServiceImpl extends BaseServiceImpl<VillageMedia, Long, VillageMediaRepository>
        implements VillageMediaService {
    private final VillageMediaRepository villageMediaRepository;
    private final StorageService storageService;
    private final HomestayRepository homestayRepository;
    private final HomestayMediaRepository homestayMediaRepository;

    @Override
    public List<String> getAllUrlVillageMedia() {
        return villageMediaRepository.getAllUrlVillageMedia();
    }

    @Override
    public VillageMediaCreateAndUpdateResponse getAllVillageMedia() {
        VillageMediaCreateAndUpdateResponse response = new VillageMediaCreateAndUpdateResponse();
        List<VillageMedia> imageHomeMain = villageMediaRepository.findByVillageMediaByPosition(MediaVillagePosition.HOME_MAIN);
        List<VillageMedia> imageHomeSub = villageMediaRepository.findByVillageMediaByPosition(MediaVillagePosition.HOME_SUB);
        List<VillageMedia> imageGallery = villageMediaRepository.findByVillageMediaByPosition(MediaVillagePosition.GALLERY);
        response.setImageHomeMain(imageHomeMain);
        response.setImageHomeSub(imageHomeSub);
        response.setImageGallery(imageGallery);
        return response;
    }

    @Override
    public void createAndUpdateVillageMedia(VillageMediaCreateAndUpdateResponse dto) {
        try {
            //Image Home Main
            if (dto.getImageHomeMainUpload() != null && !dto.getImageHomeMainUpload().isEmpty()) {
                MultipartFile imageHomeMain = dto.getImageHomeMainUpload().get(0);
                if (!villageMediaRepository.findByVillageMediaByPosition(MediaVillagePosition.HOME_MAIN).isEmpty()) {
                    deleteVillageMedia(villageMediaRepository.findByVillageMediaByPosition(MediaVillagePosition.HOME_MAIN).get(0));
                }
                createVillageMedia(imageHomeMain.getOriginalFilename(),
                        storageService.uploadFile(imageHomeMain), MediaVillagePosition.HOME_MAIN);
            }
            //Image Home Sub
            List<VillageMedia> villageMediaHomeSubListOldToDelete = villageMediaRepository.findByVillageMediaByPosition(MediaVillagePosition.HOME_SUB);
            List<VillageMedia> villageMediaHomeSubListNew = dto.getImageHomeSub();
            villageMediaHomeSubListOldToDelete.removeIf(villageMediaHomeSubListNew::contains);
            if (dto.getImageHomeSubUploads() != null && !dto.getImageHomeSubUploads().isEmpty()) {
                int totalImageHomeSub = dto.getImageHomeSubUploads().size() + villageMediaHomeSubListNew.size();
                if (totalImageHomeSub > 4) {
                    for (int i = 0; i < villageMediaHomeSubListOldToDelete.size(); i++) {
                        if (createVillageMedia(dto.getImageHomeSubUploads().get(i).getOriginalFilename(),
                                storageService.uploadFile(dto.getImageHomeSubUploads().get(i)), MediaVillagePosition.HOME_SUB)) {
                            deleteVillageMedia(villageMediaHomeSubListOldToDelete.get(i));
                        }
                    }
                } else {
                    for (int i = 0; i < dto.getImageHomeSubUploads().size(); i++) {
                        if (createVillageMedia(dto.getImageHomeSubUploads().get(i).getOriginalFilename(),
                                storageService.uploadFile(dto.getImageHomeSubUploads().get(i)), MediaVillagePosition.HOME_SUB)) {
                            deleteVillageMedia(villageMediaHomeSubListOldToDelete.get(i));
                        }
                    }
                }
            }
            //Image Gallery
            List<VillageMedia> villageMediaGalleryListOldToDelete = villageMediaRepository.findByVillageMediaByPosition(MediaVillagePosition.GALLERY);
            List<VillageMedia> villageMediaGalleryListNew = dto.getImageGallery();
            villageMediaGalleryListOldToDelete.removeIf(villageMediaGalleryListNew::contains);
            if (!villageMediaGalleryListOldToDelete.isEmpty()) {
                for (VillageMedia villageMedia : villageMediaGalleryListOldToDelete) {
                    deleteVillageMedia(villageMedia);
                }
            }
            if(dto.getImageGalleryUploads() != null && !dto.getImageGalleryUploads().isEmpty()) {
                for (int i = 0; i < dto.getImageGalleryUploads().size(); i++) {
                    createVillageMedia(dto.getImageGalleryUploads().get(i).getOriginalFilename(),
                            storageService.uploadFile(dto.getImageGalleryUploads().get(i)), MediaVillagePosition.GALLERY);
                }
            }
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("media.create.and.update.error");
        }
    }

    private void deleteVillageMedia(VillageMedia villageMedia) {
        try {
            storageService.deleteFile(villageMedia.getFilePath());
            villageMediaRepository.delete(villageMedia);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("media.delete.error");
        }
    }

    private boolean createVillageMedia(String fileName, String filePath, MediaVillagePosition position) {
        if (!Objects.equals(fileName, "")) {
            VillageMedia villageMedia = new VillageMedia();
            villageMedia.setFileName(fileName);
            villageMedia.setFilePath(filePath);
            villageMedia.setType(MediaType.IMAGE);
            villageMedia.setPosition(position);
            villageMediaRepository.save(villageMedia);
            return true;
        }
        return false;
    }

    @Override
    public List<String> getVillageMediaHomePage() {
        List<String> images = villageMediaRepository.getVillageMediaHomePage();
        String imageHomeMain = images.get(0);
        String imageHomeSub = images.get(2);
        images.add(0, imageHomeSub);
        images.add(2, imageHomeMain);
        return images;
    }

    @Override
    public List<Map<String, String>> uploadImage(List<MultipartFile> image) throws IOException {
        List<Map<String, String>> response = new ArrayList<>();
        for (MultipartFile multipartFile : image) {
            Map<String, String> map = new HashMap<>();
            map.put("fileName", multipartFile.getOriginalFilename());
            map.put("filePath", storageService.uploadFile(multipartFile));
            response.add(map);
        }
        return response;
    }

    @Override
    public void uploadImageHomestay(Long homestayId, List<MultipartFile> image) throws IOException {
        Homestay homestay = homestayRepository.findById(homestayId).get();
        for (MultipartFile multipartFile : image) {
            HomestayMedia homestayMedia = new HomestayMedia();
            homestayMedia.setFileName(multipartFile.getOriginalFilename());
            homestayMedia.setFilePath(storageService.uploadFile(multipartFile));
            homestayMedia.setType(MediaType.IMAGE);
            homestayMedia.setHomestay(homestay);
            homestayMediaRepository.save(homestayMedia);
        }
    }

    @Override
    public void uploadImageForProduct(List<MultipartFile> image) throws IOException {
        for (MultipartFile multipartFile : image) {
            VillageMedia localProductMedia = new VillageMedia();
            localProductMedia.setFileName(multipartFile.getOriginalFilename());
            localProductMedia.setFilePath(storageService.uploadFile(multipartFile));
            localProductMedia.setType(MediaType.IMAGE);
            villageMediaRepository.save(localProductMedia);
        }
    }

}

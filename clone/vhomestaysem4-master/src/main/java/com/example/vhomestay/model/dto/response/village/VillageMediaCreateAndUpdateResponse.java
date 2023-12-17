package com.example.vhomestay.model.dto.response.village;

import com.example.vhomestay.model.entity.VillageMedia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VillageMediaCreateAndUpdateResponse {
    private List<VillageMedia> imageHomeMain;
    private List<MultipartFile> imageHomeMainUpload;
    private List<VillageMedia> imageHomeSub;
    private List<MultipartFile> imageHomeSubUploads;
    private List<VillageMedia> imageGallery;
    private List<MultipartFile> imageGalleryUploads;
}

package com.example.vhomestay.model.dto.response;

import com.example.vhomestay.enums.HomestayStatus;
import com.example.vhomestay.model.entity.HomestayMedia;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomestayInformationForManager {
    private Long homestayId;
    private String homestayCode;
    private Integer totalRoom;
    private Integer totalDorm;
    private HomestayStatus homestayStatus;
    private String address;
    private List<HomestayMedia> homestayMediaList;
    private List<MultipartFile> imagesFile;

    public HomestayInformationForManager(Long homestayId, String homestayCode, Integer totalRoom, Integer totalDorm, HomestayStatus homestayStatus, String address, List<HomestayMedia> homestayMediaList) {
        this.homestayId = homestayId;
        this.homestayCode = homestayCode;
        this.totalRoom = totalRoom;
        this.totalDorm = totalDorm;
        this.homestayStatus = homestayStatus;
        this.address = address;
        this.homestayMediaList = homestayMediaList;
    }
}

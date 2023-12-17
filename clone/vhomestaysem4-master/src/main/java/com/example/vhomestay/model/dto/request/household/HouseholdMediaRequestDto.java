package com.example.vhomestay.model.dto.request.household;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class HouseholdMediaRequestDto {
    private MultipartFile avatar;
    private MultipartFile coverImage;
}

package com.example.vhomestay.model.dto.request.area;

import com.example.vhomestay.util.annotation.UniqueName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@ToString
@Data
public class AreaAdminRequestDto {
    private Long id;
    private String name;
    private MultipartFile image;
}

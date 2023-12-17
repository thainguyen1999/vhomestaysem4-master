package com.example.vhomestay.model.dto.request.news;

import com.example.vhomestay.enums.NewsSubject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsForCreateAndUpdateRequestDto {
    private String title;
    private NewsSubject subject;
    private String shortDescription;
    private String thumbnail;
    private MultipartFile thumbnailFile;
    private String content;
    private Integer readTime;
}

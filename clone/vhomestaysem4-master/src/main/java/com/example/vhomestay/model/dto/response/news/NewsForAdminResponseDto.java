package com.example.vhomestay.model.dto.response.news;

import com.example.vhomestay.enums.NewsSubject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsForAdminResponseDto {
    private Long id;
    private String title;
    private NewsSubject subject;
    private String shortDescription;
    private LocalDateTime createdDate;

}

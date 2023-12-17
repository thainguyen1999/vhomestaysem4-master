package com.example.vhomestay.service;

import com.example.vhomestay.enums.NewsSubject;
import com.example.vhomestay.model.dto.request.news.NewsForCreateAndUpdateRequestDto;
import com.example.vhomestay.model.dto.response.news.NewsForAdminResponseDto;
import com.example.vhomestay.model.dto.response.news.NewsForGuestResponseDto;
import com.example.vhomestay.model.entity.News;

import java.util.List;

public interface NewsService extends BaseService<News, Long>{
    List<NewsForAdminResponseDto> getNewsByAdmin();

    News getNewsDetail(Long newsId);

    void createNewsByAdmin(NewsForCreateAndUpdateRequestDto newsForCreateRequestDto);

    NewsForCreateAndUpdateRequestDto getNewsForUpdateByAdmin(Long newsId);

    void updateNewsByAdmin(Long newsId, NewsForCreateAndUpdateRequestDto newsForCreateRequestDto);

    void deleteNewsByAdmin(Long newsId);

    List<NewsForGuestResponseDto> getNewsByGuest();

    List<NewsForGuestResponseDto> getThreeNewsSameSubjectAndNotThisNews(NewsSubject subject, Long newsId);

    List<NewsForGuestResponseDto> getFourNewsLatest();
}

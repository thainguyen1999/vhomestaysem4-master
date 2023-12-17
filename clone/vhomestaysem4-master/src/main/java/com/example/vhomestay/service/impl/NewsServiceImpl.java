package com.example.vhomestay.service.impl;

import com.example.vhomestay.constant.DateTimeConstant;
import com.example.vhomestay.enums.NewsSubject;
import com.example.vhomestay.model.dto.request.news.NewsForCreateAndUpdateRequestDto;
import com.example.vhomestay.model.dto.response.news.NewsForAdminResponseDto;
import com.example.vhomestay.model.dto.response.news.NewsForGuestResponseDto;
import com.example.vhomestay.model.entity.News;
import com.example.vhomestay.repository.AdminRepository;
import com.example.vhomestay.repository.NewsRepository;
import com.example.vhomestay.service.NewsService;
import com.example.vhomestay.service.StorageService;
import com.example.vhomestay.util.exception.ResourceInternalServerErrorException;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl extends BaseServiceImpl<News, Long, NewsRepository> implements NewsService {
    private final NewsRepository newsRepository;
    private final AdminRepository adminRepository;
    private final StorageService storageService;

    @Override
    public List<NewsForAdminResponseDto> getNewsByAdmin() {
        return newsRepository.getNewsByAdmin();
    }

    @Override
    public News getNewsDetail(Long newsId) {
        News news = newsRepository.getNewsDetail(newsId)
                .orElseThrow(() -> new ResourceNotFoundException("news.not.found"));
        news.setCreatedDate(news.getCreatedDate().plusHours(DateTimeConstant.HOURS_DEPLOY));
        news.setLastModifiedDate(news.getLastModifiedDate().plusHours(DateTimeConstant.HOURS_DEPLOY));
        adminRepository.findByEmail(news.getCreatedBy())
                .ifPresent(adminCreate -> news.setCreatedBy(adminCreate.getFirstName() + " " + adminCreate.getLastName()));
        if (news.getLastModifiedBy() != null) {
            adminRepository.findByEmail(news.getLastModifiedBy())
                    .ifPresent(adminUpdate -> news.setLastModifiedBy(adminUpdate.getFirstName() + " " + adminUpdate.getLastName()));
        }
        return news;
    }

    @Override
    public void createNewsByAdmin(NewsForCreateAndUpdateRequestDto newsForCreateRequestDto) {
        try {
            News news = new News();
            news.setTitle(newsForCreateRequestDto.getTitle());
            news.setSubject(newsForCreateRequestDto.getSubject());
            news.setShortDescription(newsForCreateRequestDto.getShortDescription());
            news.setContent(newsForCreateRequestDto.getContent());
            news.setReadTime(newsForCreateRequestDto.getReadTime());
            MultipartFile imageFile = newsForCreateRequestDto.getThumbnailFile();
            if (!imageFile.isEmpty()) {
                String thumbnailUrl = storageService.uploadFile(imageFile);
                news.setThumbnail(thumbnailUrl);
            }
            newsRepository.save(news);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("news.create.error");
        }
    }

    @Override
    public NewsForCreateAndUpdateRequestDto getNewsForUpdateByAdmin(Long newsId) {
        News news = newsRepository.getNewsDetail(newsId)
                .orElseThrow(() -> new ResourceNotFoundException("news.not.found"));
        NewsForCreateAndUpdateRequestDto newsForCreateAndUpdateRequestDto = new NewsForCreateAndUpdateRequestDto();
        newsForCreateAndUpdateRequestDto.setTitle(news.getTitle());
        newsForCreateAndUpdateRequestDto.setSubject(news.getSubject());
        newsForCreateAndUpdateRequestDto.setShortDescription(news.getShortDescription());
        newsForCreateAndUpdateRequestDto.setThumbnail(news.getThumbnail());
        newsForCreateAndUpdateRequestDto.setContent(news.getContent());
        newsForCreateAndUpdateRequestDto.setReadTime(news.getReadTime());
        return newsForCreateAndUpdateRequestDto;
    }

    @Override
    public void updateNewsByAdmin(Long newsId, NewsForCreateAndUpdateRequestDto newsForCreateRequestDto) {
        try {
            News news = newsRepository.getNewsDetail(newsId)
                    .orElseThrow(() -> new ResourceNotFoundException("news.not.found"));
            news.setTitle(newsForCreateRequestDto.getTitle());
            news.setSubject(news.getSubject());
            news.setShortDescription(newsForCreateRequestDto.getShortDescription());
            news.setContent(newsForCreateRequestDto.getContent());
            news.setReadTime(newsForCreateRequestDto.getReadTime());
            if (newsForCreateRequestDto.getThumbnailFile() != null && !newsForCreateRequestDto.getThumbnailFile().isEmpty()) {
                MultipartFile imageFile = newsForCreateRequestDto.getThumbnailFile();
                String thumbnailUrl = storageService.uploadFile(imageFile);
                storageService.deleteFile(news.getThumbnail());
                news.setThumbnail(thumbnailUrl);
            }
            newsRepository.save(news);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("news.update.error");
        }
    }

    @Override
    public void deleteNewsByAdmin(Long newsId) {
        News news = newsRepository.getNewsDetail(newsId)
                .orElseThrow(() -> new ResourceNotFoundException("news.not.found"));
        try {
            storageService.deleteFile(news.getThumbnail());
            newsRepository.delete(news);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("news.delete.error");
        }
    }

    @Override
    public List<NewsForGuestResponseDto> getNewsByGuest() {
        return newsRepository.getNewsByGuest();
    }

    @Override
    public List<NewsForGuestResponseDto> getThreeNewsSameSubjectAndNotThisNews(NewsSubject subject, Long newsId) {
        List<NewsForGuestResponseDto> newsForGuestResponseDtoList = newsRepository.getThreeNewsSameSubjectAndNotThisNews(subject, newsId);
        int additionalNewsNeeded = 3 - newsForGuestResponseDtoList.size();

        if (additionalNewsNeeded > 0) {
            List<NewsForGuestResponseDto> newsForGuestResponseDtoList2 = newsRepository.getThreeNewsByGuestAndDifferentSubjectANDNotThisNews(subject, newsId);
            int additionalNewsFromList2 = Math.min(additionalNewsNeeded, newsForGuestResponseDtoList2.size());
            newsForGuestResponseDtoList.addAll(newsForGuestResponseDtoList2.subList(0, additionalNewsFromList2));
        }
        return newsForGuestResponseDtoList;
    }

    @Override
    public List<NewsForGuestResponseDto> getFourNewsLatest() {
        return newsRepository.getFourNewsLatest();
    }
}

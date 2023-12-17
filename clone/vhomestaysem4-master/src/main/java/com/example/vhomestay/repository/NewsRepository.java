package com.example.vhomestay.repository;

import com.example.vhomestay.enums.NewsSubject;
import com.example.vhomestay.model.dto.response.news.NewsForAdminResponseDto;
import com.example.vhomestay.model.dto.response.news.NewsForGuestResponseDto;
import com.example.vhomestay.model.entity.News;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends BaseRepository<News, Long>{
    @Query("SELECT new com.example.vhomestay.model.dto.response.news.NewsForAdminResponseDto(n.id, n.title, n.subject, n.shortDescription, n.createdDate) " +
            "FROM News n ORDER BY n.createdDate DESC")
    List<NewsForAdminResponseDto> getNewsByAdmin();

    @Query("SELECT n FROM News n WHERE n.id = :newsId")
    Optional<News> getNewsDetail(Long newsId);

    @Query("SELECT new com.example.vhomestay.model.dto.response.news.NewsForGuestResponseDto(n.id, n.title, n.subject, n.shortDescription, n.thumbnail, n.readTime, n.createdDate) " +
            "FROM News n ORDER BY n.createdDate DESC")
    List<NewsForGuestResponseDto> getNewsByGuest();

    @Query("SELECT new com.example.vhomestay.model.dto.response.news.NewsForGuestResponseDto(n.id, n.title, n.subject, n.shortDescription, n.thumbnail, n.readTime, n.createdDate) " +
            "FROM News n WHERE n.subject = :subject AND n.id != :newsId ORDER BY n.createdDate DESC LIMIT 3")
    List<NewsForGuestResponseDto> getThreeNewsSameSubjectAndNotThisNews(NewsSubject subject, Long newsId);

    @Query("SELECT new com.example.vhomestay.model.dto.response.news.NewsForGuestResponseDto(n.id, n.title, n.subject, n.shortDescription, n.thumbnail, n.readTime, n.createdDate) " +
            "FROM News n WHERE n.subject != :subject AND n.id != :newsId ORDER BY n.createdDate DESC LIMIT 3")
    List<NewsForGuestResponseDto> getThreeNewsByGuestAndDifferentSubjectANDNotThisNews(NewsSubject subject, Long newsId);

    @Query("SELECT count(n) FROM News n")
    Integer countALlNews();

    @Query("SELECT new com.example.vhomestay.model.dto.response.news.NewsForGuestResponseDto(n.id, n.title, n.subject, n.shortDescription, n.thumbnail, n.readTime, n.createdDate) " +
            "FROM News n ORDER BY n.createdDate DESC LIMIT 4")
    List<NewsForGuestResponseDto> getFourNewsLatest();
}

package com.example.vhomestay.controller.guest;

import com.example.vhomestay.model.dto.response.news.NewsForGuestResponseDto;
import com.example.vhomestay.model.entity.News;
import com.example.vhomestay.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsForGuestController {
    private final NewsService newsService;

    @GetMapping()
    public ResponseEntity<?> getNewsByGuest(){
        List<NewsForGuestResponseDto> news = newsService.getNewsByGuest();
        return ResponseEntity.ok(news);
    }

    @GetMapping("/{newsId}")
    public ResponseEntity<?> getNewsDetailByGuest(@PathVariable("newsId") Long newsId){
        News news = newsService.getNewsDetail(newsId);
        List<NewsForGuestResponseDto> threeNewsSameSubject = newsService.getThreeNewsSameSubjectAndNotThisNews(news.getSubject(), newsId);
        Map<String, Object> response = Map.of("news", news, "threeNews", threeNewsSameSubject);
        return ResponseEntity.ok(response);
    }

}

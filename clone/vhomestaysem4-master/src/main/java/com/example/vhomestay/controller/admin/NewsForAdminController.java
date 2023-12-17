package com.example.vhomestay.controller.admin;

import com.example.vhomestay.enums.NewsSubject;
import com.example.vhomestay.model.dto.request.news.NewsForCreateAndUpdateRequestDto;
import com.example.vhomestay.model.dto.response.MessageResponseDto;
import com.example.vhomestay.model.dto.response.news.NewsForAdminResponseDto;
import com.example.vhomestay.model.entity.News;
import com.example.vhomestay.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/news")
@RequiredArgsConstructor
public class NewsForAdminController {
    private final NewsService newsService;
    private final MessageSource messageSource;

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN') OR hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getNewsByAdmin(){
        List<NewsForAdminResponseDto> news = newsService.getNewsByAdmin();
        List<NewsSubject> newsSubjects = List.of(NewsSubject.values());
        List<Map<String, String>> newsSubjectMapList = new ArrayList<>();
        Map<String, String> newsSubjectMap;
        for (NewsSubject newsSubject : newsSubjects) {
            newsSubjectMap = Map.of("code", newsSubject.toString(), "name", newsSubject.getSubject());
            newsSubjectMapList.add(newsSubjectMap);
        }
        Map<String, Object> response = Map.of("news", news, "newsSubjects", newsSubjectMapList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{newsId}")
    @PreAuthorize("hasRole('ADMIN') OR hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getNewsDetailByAdmin(@PathVariable("newsId") Long newsId){
        News news = newsService.getNewsDetail(newsId);
        Map<String, Object> response = Map.of("news", news);
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN') OR hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createNewsByAdmin(@ModelAttribute NewsForCreateAndUpdateRequestDto newsForCreateRequestDto){
        newsService.createNewsByAdmin(newsForCreateRequestDto);
        return ResponseEntity.ok("news.create.success");

    }

    @GetMapping("/{newsId}/form-update")
    @PreAuthorize("hasRole('ADMIN') OR hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getNewsForUpdateByAdmin(@PathVariable("newsId") Long newsId){
        NewsForCreateAndUpdateRequestDto news = newsService.getNewsForUpdateByAdmin(newsId);
        List<NewsSubject> newsSubjects = List.of(NewsSubject.values());
        List<Map<String, String>> newsSubjectMapList = new ArrayList<>();
        Map<String, String> newsSubjectMap;
        for (NewsSubject newsSubject : newsSubjects) {
            newsSubjectMap = Map.of("code", newsSubject.toString(), "name", newsSubject.getSubject());
            newsSubjectMapList.add(newsSubjectMap);
        }
        Map<String, Object> response = Map.of("news", news, "newsSubjects", newsSubjectMapList);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{newsId}")
    @PreAuthorize("hasRole('ADMIN') OR hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateNewsByAdmin(@PathVariable("newsId") Long newsId, @ModelAttribute NewsForCreateAndUpdateRequestDto newsForCreateRequestDto){
        newsService.updateNewsByAdmin(newsId, newsForCreateRequestDto);
        return ResponseEntity.ok("news.update.success");
    }

    @DeleteMapping("/{newsId}")
    @PreAuthorize("hasRole('ADMIN') OR hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteNewsByAdmin(@PathVariable("newsId") Long newsId){
        newsService.deleteNewsByAdmin(newsId);
        return ResponseEntity.ok("service.delete.success");

    }
}

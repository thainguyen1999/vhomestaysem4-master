package com.example.vhomestay.controller.guest;

import com.example.vhomestay.model.dto.response.LocationAndWeatherDto;
import com.example.vhomestay.model.dto.response.booking.customer.HouseholdNameDto;
import com.example.vhomestay.model.dto.response.household.customer.HomestayIntroductionDto;
import com.example.vhomestay.model.dto.response.household.customer.HouseholdInTopDto;
import com.example.vhomestay.model.dto.response.news.NewsForGuestResponseDto;
import com.example.vhomestay.model.dto.response.service.customer.ServiceIntroductionDto;
import com.example.vhomestay.model.entity.LocalProduct;
import com.example.vhomestay.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class HomeController {
    private final HouseholdService householdService;
    private final LocationAndWeatherService locationAndWeatherService;
    private final AreaService areaService;
    private final ServiceForCustomerService serviceForCustomerService;
    private final LocalProductService localProductService;
    private final VillageMediaService villageMediaService;
    private final NewsService newsService;

    @GetMapping()
    public ResponseEntity<?> getHomePage() {
        List<HouseholdNameDto> householdList = householdService.findAllHouseholdName();
        LocationAndWeatherDto locationAndWeather = locationAndWeatherService.getLocationAndWeather();
        List<HomestayIntroductionDto> areaList = areaService.getAreaIntroductionList();
        List<HouseholdInTopDto> householdListInTop = householdService.findAllHouseholdInTop();
        List<ServiceIntroductionDto> serviceList = serviceForCustomerService.getAllServiceInHomePage();
        List<LocalProduct> localProductList = localProductService.getLocalProductTOP5InHomePage();
        List<String> villageMediaList = villageMediaService.getVillageMediaHomePage();
        List<NewsForGuestResponseDto> newsList = newsService.getFourNewsLatest();
        Map<String, Object> response = Map.of(
                "householdListForSearchBooking", householdList,
                "locationAndWeather", locationAndWeather,
                "areaListForMap", areaList,
                "householdOutstanding", householdListInTop,
                "serviceList", serviceList,
                "localProductOutstanding", localProductList,
                "villageMediaOutstanding", villageMediaList,
                "newsLatest", newsList
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadImage(@ModelAttribute("image")List<MultipartFile> image) throws IOException {
        List<Map<String, String>> response = villageMediaService.uploadImage(image);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/uploadImageForHomestay/{homestayId}")
    public ResponseEntity<?> uploadImageForHomestay(@PathVariable("homestayId") Long homestayId, @ModelAttribute("image")List<MultipartFile> image) throws IOException {
        villageMediaService.uploadImageHomestay(homestayId, image);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/uploadImageVillage")
    public ResponseEntity<?> uploadImageForProduct(@ModelAttribute("image")List<MultipartFile> image) throws IOException {
        villageMediaService.uploadImageForProduct(image);
        return ResponseEntity.ok().build();
    }

}

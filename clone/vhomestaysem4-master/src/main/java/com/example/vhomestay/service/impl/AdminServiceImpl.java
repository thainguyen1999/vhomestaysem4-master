package com.example.vhomestay.service.impl;

import com.example.vhomestay.config.WeatherConfig;
import com.example.vhomestay.model.dto.response.UserResponseDto;
import com.example.vhomestay.model.dto.response.dashboard.admin.DashboardForAdmin;
import com.example.vhomestay.model.dto.response.dashboard.admin.RequestDetailForAdmin;
import com.example.vhomestay.model.dto.response.user.UserInfoResponseDto;
import com.example.vhomestay.model.entity.Admin;
import com.example.vhomestay.repository.*;
import com.example.vhomestay.security.SecurityUtil;
import com.example.vhomestay.service.AdminService;
import com.example.vhomestay.service.StorageService;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import com.example.vhomestay.util.exception.ResourceInternalServerErrorException;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminServiceImpl extends BaseServiceImpl<Admin, Long, AdminRepository>
        implements AdminService {

    private final AdminRepository adminRepository;
    private final BookingRepository bookingRepository;
    private final AreaRepository areaRepository;
    private final HouseholdRepository householdRepository;
    private final HomestayRepository homestayRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final CustomerRepository customerRepository;
    private final ServiceRepository serviceRepository;
    private final LocalProductRepository localProductRepository;
    private final NewsRepository newsRepository;
    private final RequestRepository requestRepository;
    private final ModelMapper modelMapper;
    private final StorageService storageService;

    public AdminServiceImpl(AdminRepository adminRepository, BookingRepository bookingRepository, AreaRepository areaRepository, HouseholdRepository householdRepository, HomestayRepository homestayRepository, RoomTypeRepository roomTypeRepository, CustomerRepository customerRepository, ServiceRepository serviceRepository, LocalProductRepository localProductRepository, NewsRepository newsRepository, RequestRepository requestRepository, ModelMapper modelMapper, StorageService storageService) {
        this.adminRepository = adminRepository;
        this.bookingRepository = bookingRepository;
        this.areaRepository = areaRepository;
        this.householdRepository = householdRepository;
        this.homestayRepository = homestayRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.customerRepository = customerRepository;
        this.serviceRepository = serviceRepository;
        this.localProductRepository = localProductRepository;
        this.newsRepository = newsRepository;
        this.requestRepository = requestRepository;
        this.modelMapper = modelMapper;
        this.storageService = storageService;
        configureModelMapper();
    }

    private void configureModelMapper() {
        modelMapper.createTypeMap(Admin.class, UserResponseDto.class)
                .addMapping(Admin::getId, UserResponseDto::setId)
                .addMapping(Admin::getAvatar, UserResponseDto::setAvatar)
                .addMapping(Admin::getFirstName, UserResponseDto::setFirstName)
                .addMapping(Admin::getLastName, UserResponseDto::setLastName)
                .addMapping(Admin::getPhoneNumber, UserResponseDto::setPhoneNumber)
                .addMapping(Admin::getGender, UserResponseDto::setGender)
                .addMapping(Admin::getDateOfBirth, UserResponseDto::setDateOfBirth)
                .addMapping(Admin::getAddress, UserResponseDto::setAddress)
                .addMapping(src -> src.getAccount().getId(), UserResponseDto::setAccountId)
                .addMapping(src -> src.getAccount().getEmail(), UserResponseDto::setEmail)
                .addMapping(src -> src.getAccount().getRole(), UserResponseDto::setRole)
                .addMapping(src -> src.getAccount().getProvider(), UserResponseDto::setProvider);
    }

    @Override
    public Optional<UserResponseDto> getAdminProfile() {
        String adminEmail = SecurityUtil.getCurrentUserLogin().get();

        Admin admin = adminRepository.findByAccountEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("admin.profile.not.found"));

        return Optional.of(mapToDTO(admin));
    }

    @Override
    public Optional<UserResponseDto> getAdminByAccountEmail(String email) {
        Optional<Admin> adminOptional = adminRepository.findByAccountEmail(email);

        if (adminOptional.isEmpty()) {
            throw new ResourceNotFoundException("admin.not.found");
        }

        Admin admin = adminOptional.get();

        UserResponseDto userResponseDto = mapToDTO(admin);

        return Optional.of(userResponseDto);
    }

    @Override
    public boolean updateAdminProfile(UserResponseDto userResponseDto) {
        String adminEmail = SecurityUtil.getCurrentUserLogin().get();

        Admin admin = adminRepository.findByAccountEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("admin.profile.not.found"));

        admin.setFirstName(userResponseDto.getFirstName());
        admin.setLastName(userResponseDto.getLastName());
        admin.setPhoneNumber(userResponseDto.getPhoneNumber());
        admin.setGender(userResponseDto.getGender());
        admin.setDateOfBirth(userResponseDto.getDateOfBirth());
        admin.setAddress(userResponseDto.getAddress());

        try {
            adminRepository.save(admin);
            return true;
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("admin.profile.not.found");
        }
    }

    @Override
    public UserResponseDto mapToDTO(Admin admin) {
        return modelMapper.map(admin, UserResponseDto.class);
    }

    @Override
    public Admin mapToEntity(UserResponseDto userResponseDto) {
        return modelMapper.map(userResponseDto, Admin.class);
    }

    @Override
    public List<UserInfoResponseDto> findAllByAdmin() {
        return adminRepository.findAllByAdmin();
    }

    @Override
    public DashboardForAdmin getAdminDashboard() {

        DashboardForAdmin dashboardForAdmin = new DashboardForAdmin();
        Integer totalArea = areaRepository.countAllArea();
        Integer totalHousehold = householdRepository.countAllHousehold();
        Integer totalHomestay = homestayRepository.countAllHomestay();
        Integer totalRoomType = roomTypeRepository.countAllRoomType();
        Integer totalCustomer = customerRepository.countAllCustomer();
        Integer totalService = serviceRepository.countAllService();
        Integer totalLocalProduct = localProductRepository.countAllLocalProduct();
        Integer totalNews = newsRepository.countALlNews();
        List<RequestDetailForAdmin> requestDetailListForAdmin = requestRepository.getAllRequestForAdmin();

        dashboardForAdmin.setTotalArea(totalArea);
        dashboardForAdmin.setTotalHousehold(totalHousehold);
        dashboardForAdmin.setTotalHomestay(totalHomestay);
        dashboardForAdmin.setTotalRoomType(totalRoomType);
        dashboardForAdmin.setTotalUser(totalCustomer);
        dashboardForAdmin.setTotalService(totalService);
        dashboardForAdmin.setTotalLocalProduct(totalLocalProduct);
        dashboardForAdmin.setTotalNews(totalNews);
        dashboardForAdmin.setRequestDetailListForAdmin(requestDetailListForAdmin);

        WeatherConfig weatherConfig = new WeatherConfig();
        Map<String, String> weatherJson = weatherConfig.getWeather(WeatherConfig.LAT_VILLAGE, WeatherConfig.LON_VILLAGE);
        dashboardForAdmin.setWeather(weatherJson.get("weatherDescription"));
        dashboardForAdmin.setTemperature(weatherJson.get("temperature"));

        LocalTime currentTime = LocalTime.now();
        String timeOfDay;
        if (currentTime.isBefore(LocalTime.NOON)) {
            timeOfDay = "buổi sáng";
        } else if (currentTime.isBefore(LocalTime.of(18, 0))) {
            timeOfDay = "buổi chiều";
        } else {
            timeOfDay = "buổi tối";
        }
        dashboardForAdmin.setSession(timeOfDay);

        int currentYear = Year.now().getValue();
        List<Integer> totalGuestByMonthForThisYear = getTotalGuestByMonth(currentYear);
        List<Integer> totalGuestByMonthForLastYear = getTotalGuestByMonth(currentYear-1);

        dashboardForAdmin.setGetTotalGuestByMonthForThisYear(totalGuestByMonthForThisYear);
        dashboardForAdmin.setGetTotalGuestByMonthForLastYear(totalGuestByMonthForLastYear);

        return dashboardForAdmin;
    }

    @Override
    public List<Integer> getTotalGuestByMonth(int year) {
        if (year == 0) {
            year = LocalDate.now().getYear();
        }
        List<Integer> totalGuestByMonth = new ArrayList<>();
        int i;
        for (i = 0; i < 12; i++) {
            Integer totalGuest = bookingRepository.countAllBookingGuestByMonthAndYear((i + 1), year);
            if (totalGuest == null) {
                totalGuestByMonth.add(0);
                continue;
            }
            totalGuestByMonth.add(totalGuest);
        }
        return totalGuestByMonth;
    }

    @Override
    public void updateAdminAvatar(MultipartFile image) {
        String adminEmail = SecurityUtil.getCurrentUserLogin().get();

        Admin admin = adminRepository.findByAccountEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("admin.profile.not.found"));

        if (image == null || image.isEmpty()) {
            throw new ResourceBadRequestException("image.empty");
        }

        try {
            String oldImage = admin.getAvatar();

            String newImage = storageService.updateFile(oldImage, image);
            admin.setAvatar(newImage);

            adminRepository.save(admin);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("admin.profile.avatar.update.success");
        }
    }
}

package com.example.vhomestay.service.impl;

import com.example.vhomestay.config.WeatherConfig;
import com.example.vhomestay.model.dto.response.UserResponseDto;
import com.example.vhomestay.model.dto.response.dashboard.manager.*;
import com.example.vhomestay.model.dto.response.user.UserInfoResponseDto;
import com.example.vhomestay.model.entity.Feedback;
import com.example.vhomestay.model.entity.Homestay;
import com.example.vhomestay.model.entity.Manager;
import com.example.vhomestay.model.entity.Room;
import com.example.vhomestay.repository.*;
import com.example.vhomestay.security.SecurityUtil;
import com.example.vhomestay.service.ManagerService;
import com.example.vhomestay.service.StorageService;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import com.example.vhomestay.util.exception.ResourceInternalServerErrorException;
import com.example.vhomestay.util.exception.ResourceUnauthorizedException;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@Log4j2
public class ManagerServiceImpl implements ManagerService {

    private final ManagerRepository managerRepository;
    private final HomestayRepository homestayRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final FeedbackRepository feedbackRepository;
    private ModelMapper modelMapper;
    private StorageService storageService;

    public ManagerServiceImpl(ManagerRepository managerRepository, HomestayRepository homestayRepository, RoomRepository roomRepository, BookingRepository bookingRepository, FeedbackRepository feedbackRepository, ModelMapper modelMapper, StorageService storageService) {
        this.managerRepository = managerRepository;
        this.homestayRepository = homestayRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.feedbackRepository = feedbackRepository;
        this.modelMapper = modelMapper;
        this.storageService = storageService;
        configureModelMapper();
    }

    private void configureModelMapper() {
        modelMapper.createTypeMap(Manager.class, UserResponseDto.class)
                .addMapping(Manager::getId, UserResponseDto::setId)
                .addMapping(Manager::getAvatar, UserResponseDto::setAvatar)
                .addMapping(Manager::getFirstName, UserResponseDto::setFirstName)
                .addMapping(Manager::getLastName, UserResponseDto::setLastName)
                .addMapping(Manager::getPhoneNumber, UserResponseDto::setPhoneNumber)
                .addMapping(Manager::getGender, UserResponseDto::setGender)
                .addMapping(Manager::getDateOfBirth, UserResponseDto::setDateOfBirth)
                .addMapping(Manager::getAddress, UserResponseDto::setAddress)
                .addMapping(src -> src.getAccount().getId(), UserResponseDto::setAccountId)
                .addMapping(src -> src.getAccount().getEmail(), UserResponseDto::setEmail)
                .addMapping(src -> src.getAccount().getRole(), UserResponseDto::setRole)
                .addMapping(src -> src.getAccount().getProvider(), UserResponseDto::setProvider);
    }

    @Override
    public Optional<UserResponseDto> getManagerProfile() {
        Optional<String> getCurrentUserEmail = SecurityUtil.getCurrentUserLogin();
        return getCurrentUserEmail.map(this::getManagerByAccountEmail)
                .orElseThrow(() -> new ResourceUnauthorizedException("manager.household.authorized"));
    }

    @Override
    public Optional<UserResponseDto> getManagerByAccountEmail(String email) {
        Optional<Manager> manager = managerRepository.findByAccountEmail(email);
        return Optional.ofNullable(manager.map(this::mapToDTO)
                .orElseThrow(() -> new ResourceUnauthorizedException("manager.household.notfound")));
    }

    @Override
    public boolean updateManagerProfile(UserResponseDto userResponseDto) {
        String emailManager = SecurityUtil.getCurrentUserLogin().get();

        Manager manager = managerRepository.findByAccountEmail(emailManager)
                .orElseThrow(() -> new ResourceUnauthorizedException("manager.household.notfound"));

        manager.setFirstName(userResponseDto.getFirstName());
        manager.setLastName(userResponseDto.getLastName());
        manager.setPhoneNumber(userResponseDto.getPhoneNumber());
        manager.setGender(userResponseDto.getGender());
        manager.setDateOfBirth(userResponseDto.getDateOfBirth());
        manager.setAddress(userResponseDto.getAddress());

        try {
            managerRepository.save(manager);
            return true;
        } catch (Exception e) {
            throw new ResourceUnauthorizedException("manager.household.update.error");
        }
    }

    @Override
    public UserResponseDto mapToDTO(Manager manager) {
        return modelMapper.map(manager, UserResponseDto.class);
    }

    @Override
    public Manager mapToEntity(UserResponseDto userResponseDto) {
        return modelMapper.map(userResponseDto, Manager.class);
    }

    @Override
    public List<UserInfoResponseDto> findAllByAdmin() {
        return managerRepository.findAllByAdmin();
    }

    @Override
    public DashboardForManager getManagerDashboard() {

        DashboardForManager dashboardForManager = new DashboardForManager();

        String managerEmail = SecurityUtil.getCurrentUserLogin().get();

        Integer totalRoom = 0, totalDorm = 0, totalCapacity = 0,
                totalCheckInToday = 0, totalCheckOutToday = 0, totalBookingToday = 0;
        Double totalFeedbackScore = 0.0;

        List<Homestay> homestayList = homestayRepository.findAllHomestayByManagerEmail(managerEmail);
        int listHomestaySize = homestayList.size(), i;
        for (i = 0; i < listHomestaySize; i++) {
            List<Room> roomList = roomRepository.findAllByHomestayId(homestayList.get(i).getId());
            int roomListSize = roomList.size(), j;
            for (j = 0; j < roomListSize; j++) {
                if (roomList.get(j).getHouseholdRoomType().getRoomType().getIsDorm()) {
                    int dormSlot = roomRepository.countDormSlotByRoomId(roomList.get(j).getId());
                    totalCapacity += dormSlot;
                    totalDorm += dormSlot;
                } else {
                    totalCapacity += roomList.get(j).getHouseholdRoomType().getCapacity();
                    totalRoom++;
                }
            }
        }

        totalBookingToday = bookingRepository.countAllBookingTodayByManager(managerEmail);
        totalCheckInToday = bookingRepository.countAllCheckInTodayByManager(managerEmail);
        totalCheckOutToday = bookingRepository.countAllCheckOutTodayByManager(managerEmail);

        List<Feedback> feedbackList = feedbackRepository.getFeedbackByManagerEmail(managerEmail);
        int feedbackListSize = feedbackList.size(), k;
        for (k = 0; k < feedbackListSize; k++) {
            totalFeedbackScore += feedbackList.get(k).getRating();
        }

        List<BookingCancelDetailForManager> bookingCancelListForManager = bookingRepository.getBookingCancelDetailForManager(managerEmail);
        List<LowFeedbackDetailForManager> lowFeedbackListForManager = feedbackRepository.getLowFeedbackDetailForManager(managerEmail);

        dashboardForManager.setTotalHomestay(listHomestaySize);
        dashboardForManager.setTotalRoom(totalRoom);
        dashboardForManager.setTotalDorm(totalDorm);
        dashboardForManager.setTotalCapacity(totalCapacity);
        dashboardForManager.setTotalCheckInToday(totalCheckInToday);
        dashboardForManager.setTotalCheckOutToday(totalCheckOutToday);
        dashboardForManager.setTotalBookingToday(totalBookingToday);
        if (feedbackListSize == 0) {
            dashboardForManager.setTotalFeedback(0);
            dashboardForManager.setTotalFeedbackScore(0.0);
        } else {
            dashboardForManager.setTotalFeedback(feedbackListSize);
            dashboardForManager.setTotalFeedbackScore(totalFeedbackScore / feedbackListSize);
        }
        dashboardForManager.setBookingCancelListForManager(bookingCancelListForManager);
        dashboardForManager.setLowFeedbackListForManager(lowFeedbackListForManager);

        WeatherConfig weatherConfig = new WeatherConfig();
        Map<String, String> weatherJson = weatherConfig.getWeather(WeatherConfig.LAT_VILLAGE, WeatherConfig.LON_VILLAGE);
        dashboardForManager.setWeather(weatherJson.get("weatherDescription"));
        dashboardForManager.setTemperature(weatherJson.get("temperature"));

        LocalTime currentTime = LocalTime.now();
        String timeOfDay;
        if (currentTime.isBefore(LocalTime.NOON)) {
            timeOfDay = "buổi sáng";
        } else if (currentTime.isBefore(LocalTime.of(18, 0))) {
            timeOfDay = "buổi chiều";
        } else {
            timeOfDay = "buổi tối";
        }
        dashboardForManager.setSession(timeOfDay);

        return dashboardForManager;
    }

    @Override
    public List<HomestayInformationForDashboard> getHomestayInformationForDashboard() {
        List<HomestayInformationForDashboard> homestayInformationForDashboardList = new ArrayList<>();
        String managerEmail = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new ResourceUnauthorizedException("manager.household.authorized"));
        List<Homestay> homestayList = homestayRepository.findAllHomestayByManagerEmail(managerEmail);
        for (Homestay homestay : homestayList) {
            HomestayInformationForDashboard homestayInformationForDashboard = new HomestayInformationForDashboard();
            homestayInformationForDashboard.setHomestayId(homestay.getId());
            homestayInformationForDashboard.setHomestayCode(homestay.getHomestayCode());
            homestayInformationForDashboard.setRoomInformationForDashboards(getRoomInformationForDashboard(homestay.getId(), LocalDate.now()));
            homestayInformationForDashboard.setDormitoryInformationForDashboards(getDormitoryInformationForDashboard(homestay.getId(), LocalDate.now()));
            homestayInformationForDashboardList.add(homestayInformationForDashboard);
        }
        return homestayInformationForDashboardList;
    }

    @Override
    public void updateManagerAvatar(MultipartFile image) throws IOException {
        String emailManager = SecurityUtil.getCurrentUserLogin().get();

        Manager manager = managerRepository.findByAccountEmail(emailManager)
                .orElseThrow(() -> new ResourceUnauthorizedException("manager.profile.notfound"));

        if (image == null || image.isEmpty()) {
            throw new ResourceBadRequestException("required.field");
        }
        String oldImage = manager.getAvatar();

        String newImage = storageService.updateFile(oldImage, image);
        manager.setAvatar(newImage);
        try {
            managerRepository.save(manager);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("manager.profile.update.error");
        }
    }

    private List<DormitoryInformationForDashboard> getDormitoryInformationForDashboard(Long homestayId, LocalDate date) {
        List<DormitoryInformationForDashboard> listDormLooked = roomRepository.getDormitoryLockedInformationForDashboard(homestayId, date);
        List<DormitoryInformationForDashboard> listDormInformationForDashboard = roomRepository.getAllDormitoryInformationForDashboard(homestayId);
        for (DormitoryInformationForDashboard dorm : listDormInformationForDashboard) {
            for (DormitoryInformationForDashboard dormLooked : listDormLooked) {
                if (dorm.getRoomId().equals(dormLooked.getRoomId())) {
                    dorm.setAvailableSlot((int) (dorm.getTotalSlot() - dormLooked.getTotalSlot()));
                    break;
                }
            }
        }
        return listDormInformationForDashboard;
    }

    private List<RoomInformationForDashboard> getRoomInformationForDashboard(Long homestayId, LocalDate date) {
        List<RoomInformationForDashboard> listRoomLooked = roomRepository.getRoomLockedInformationForDashboard(homestayId, date);
        List<RoomInformationForDashboard> listRoom = roomRepository.getAllRoomInformationForDashboard(homestayId);
        List<RoomInformationForDashboard> listRoomAvailable = new ArrayList<>();
        for (RoomInformationForDashboard roomInformationForDashboard : listRoom) {
            boolean isExist = false;
            for (RoomInformationForDashboard roomInformationForDashboard1 : listRoomLooked) {
                if (roomInformationForDashboard.getRoomId().equals(roomInformationForDashboard1.getRoomId())) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                listRoomAvailable.add(roomInformationForDashboard);
            }
        }
        List<RoomInformationForDashboard> listRoomInformationForDashboard = new ArrayList<>();
        listRoomInformationForDashboard.addAll(listRoomLooked);
        listRoomInformationForDashboard.addAll(listRoomAvailable);
        listRoomInformationForDashboard.sort(Comparator.comparing(RoomInformationForDashboard::getRoomName));
        return listRoomInformationForDashboard;
    }
}

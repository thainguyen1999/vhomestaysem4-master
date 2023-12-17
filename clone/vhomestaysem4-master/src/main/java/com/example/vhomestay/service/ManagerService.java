package com.example.vhomestay.service;

import com.example.vhomestay.model.dto.response.UserResponseDto;
import com.example.vhomestay.model.dto.response.dashboard.manager.DashboardForManager;
import com.example.vhomestay.model.dto.response.dashboard.manager.HomestayInformationForDashboard;
import com.example.vhomestay.model.dto.response.user.UserInfoResponseDto;
import com.example.vhomestay.model.entity.Manager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ManagerService {
    Optional<UserResponseDto> getManagerProfile();
    Optional<UserResponseDto> getManagerByAccountEmail(String email);
    boolean updateManagerProfile(UserResponseDto userResponseDto);
    UserResponseDto mapToDTO(Manager manager);
    Manager mapToEntity(UserResponseDto userResponseDto);
    List<UserInfoResponseDto> findAllByAdmin();
    DashboardForManager getManagerDashboard();
    List<HomestayInformationForDashboard> getHomestayInformationForDashboard();
    void updateManagerAvatar(MultipartFile image) throws IOException;
}

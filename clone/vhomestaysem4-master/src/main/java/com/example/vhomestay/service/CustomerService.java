package com.example.vhomestay.service;

import com.example.vhomestay.model.dto.response.UserResponseDto;
import com.example.vhomestay.model.dto.response.user.UserInfoResponseDto;
import com.example.vhomestay.model.entity.Customer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CustomerService extends BaseService<Customer, Long> {
    Optional<UserResponseDto> getCustomerProfile();
    Optional<UserResponseDto> getCustomerByAccountEmail(String email);
    boolean updateCustomerProfile(Map<String, Object> updateFields);
    UserResponseDto mapToDTO(Customer customer);
    Customer mapToEntity(UserResponseDto customerFormDto);
    void uploadAvatar(MultipartFile image) throws IOException;
    String updateAvatar(MultipartFile image) throws IOException;
    boolean deleteAvatar();
    List<UserInfoResponseDto> findAllByAdmin();
}

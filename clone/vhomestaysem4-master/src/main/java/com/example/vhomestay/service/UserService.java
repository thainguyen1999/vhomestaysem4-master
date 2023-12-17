package com.example.vhomestay.service;


import com.example.vhomestay.model.dto.response.user.BookingOfCustomerDto;
import com.example.vhomestay.model.dto.request.user.UserCreateRequestDto;
import com.example.vhomestay.model.dto.response.user.UserDetailResponseDto;
import com.example.vhomestay.model.dto.response.user.UserForAdminResponseDto;
import com.example.vhomestay.model.entity.Account;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<Account> findByEmail(String email);
    UserForAdminResponseDto getUsersByAdmin();
    void inactiveOrActiveAccountOfUserByAdmin(Long accountId);
    void deleteUserByAdmin(Long accountId);
    UserDetailResponseDto getUserDetailByAdmin(Long accountId);
    List<BookingOfCustomerDto> getBookingOfCustomerByAdmin(Long accountId);
    boolean checkEmailExist(String email);
    boolean checkPhoneNumberExits(String phoneNumber);
    @Transactional
    void createUser(UserCreateRequestDto userCreateRequestDto);
}

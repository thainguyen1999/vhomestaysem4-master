package com.example.vhomestay.service.impl;

import com.example.vhomestay.constant.AppConstant;
import com.example.vhomestay.service.GenerateRandomNumber;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class GenerateRandomNumberServiceImpl implements GenerateRandomNumber {
    @Override
    public String generateRandomPassword() {
        Random random = new Random();
        StringBuilder tempPassword = new StringBuilder();

        for (int i = 0; i < AppConstant.PASSWORD_LENGTH; i++) {
            tempPassword.append(random.nextInt(10));
        }

        return tempPassword.toString();
    }

    @Override
    public String generateOTP() {
        Random random = new Random();
        StringBuilder tempPassword = new StringBuilder();

        for (int i = 0; i < AppConstant.OTP_LENGTH; i++) {
            tempPassword.append(random.nextInt(10));
        }

        return tempPassword.toString();
    }

    @Override
    public String generateRandomTokenToVerifyEmail() {
        Random random = new Random();
        StringBuilder tempPassword = new StringBuilder();

        for (int i = 0; i < AppConstant.TOKEN_LENGTH; i++) {
            tempPassword.append(random.nextInt(10));
        }

        return tempPassword.toString();
    }
}

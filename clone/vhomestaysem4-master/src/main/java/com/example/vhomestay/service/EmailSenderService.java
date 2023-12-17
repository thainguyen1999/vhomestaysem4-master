package com.example.vhomestay.service;

public interface EmailSenderService {

    void sendEmail(String to, String subject, String message);

}

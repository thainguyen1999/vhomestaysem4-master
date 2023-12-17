package com.example.vhomestay.controller;

import com.example.vhomestay.resource.EmailResource;
import com.example.vhomestay.service.EmailSenderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailSenderController {

    private final EmailSenderService emailSenderService;

    public EmailSenderController(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @PostMapping("/send-email")
    public ResponseEntity sendEmail(@RequestBody EmailResource emailResource) {
        this.emailSenderService.sendEmail(emailResource.getTo(),
                emailResource.getSubject(), emailResource.getMessage());

        return ResponseEntity.ok("email.send.success");
    }


}

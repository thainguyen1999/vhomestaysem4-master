package com.example.vhomestay.controller;

import com.example.vhomestay.model.dto.request.*;
import com.example.vhomestay.model.dto.response.LoginResponseDto;
import com.example.vhomestay.model.dto.response.MessageResponseDto;
import com.example.vhomestay.model.dto.response.UserResponseDto;
import com.example.vhomestay.model.entity.Account;
import com.example.vhomestay.security.SecurityUtil;
import com.example.vhomestay.service.AccountService;
import com.example.vhomestay.service.impl.AccountServiceImpl;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponseWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AccountService accountService;
    private final MessageSource messageSource;

    @PostMapping(value = "/register", produces = "text/plain; charset=UTF-8")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto request) throws MessagingException {
        accountService.register(request);
        return ResponseEntity.ok("account.register.success");
    }

    @PostMapping(value = "/confirm-account")
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token") String confirmationToken,
                                                @RequestParam("firstName") String firstName,
                                                @RequestParam("lastName") String lastName) {
        if (accountService.confirmEmail(confirmationToken, firstName, lastName)) {
            return ResponseEntity.ok("email.confirm.success");
        } else {
            throw new ResourceBadRequestException("account.email.verify.error");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(accountService.login(request));
    }

    @PostMapping("/re-send-confirm-email")
    public ResponseEntity<?> reSendConfirmEmail(@RequestParam("email") String email, @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName) throws MessagingException {
        if (accountService.reSendConfirmEmail(email, firstName, lastName)) {
            return ResponseEntity.ok("email.confirm.success");
        } else {
            throw new ResourceNotFoundException("account.email.notFound");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) throws MessagingException {
        if (accountService.resetPassword(request)) {
            return ResponseEntity.ok("account.reset.password.success");
        } else {
            throw new ResourceNotFoundException("account.email.notFound");
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTPForResetPassword(@RequestBody VerifyOTPRequestDto request) {
        Optional<Account> optionalAccount = accountService.findByOtp(request.getOtp());
        if (optionalAccount.isPresent()) {
            return ResponseEntity.ok("account.otp.valid");
        } else {
            throw new ResourceNotFoundException(messageSource.getMessage("account.otp.notValid", null, Locale.getDefault()));
        }
    }

    @PostMapping("/change-password-for-reset")
    public ResponseEntity<?> changePasswordForResetPassword(@Valid @RequestBody ChangePasswordForResetRequestDto request) {
        if (accountService.changePasswordForResetPassword(request)) {
            return ResponseEntity.ok("password.change.success");
        } else {
            throw new ResourceBadRequestException("password.change.error.confirmPassword");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequestDto request) {
        Optional<String> currentEmailLogin = SecurityUtil.getCurrentUserLogin();
        if (currentEmailLogin.isEmpty()){
            throw new ResourceBadRequestException("login.invalid");
        }
        if (accountService.changePassword(request, currentEmailLogin.get())) {
            return ResponseEntity.ok("password.change.success");
        } else {
            throw new ResourceBadRequestException("account.password.notMatch");
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponseDto> refreshToken(@RequestBody RefreshTokenRequest refreshToken) throws IOException {
        return ResponseEntity.ok(accountService.refreshToken(refreshToken.getRefreshToken()));
    }

    @PostMapping("/social-login")
    public ResponseEntity<LoginResponseDto> SocialLogin(@RequestBody RegisterWithoutVerifyRequest request) {
        return ResponseEntity.ok(accountService.socialLogin(request));
    }
}

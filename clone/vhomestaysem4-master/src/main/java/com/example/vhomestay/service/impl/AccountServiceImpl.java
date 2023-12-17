package com.example.vhomestay.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.vhomestay.config.DeployConfig;
import com.example.vhomestay.enums.AccountRole;
import com.example.vhomestay.enums.AccountStatus;
import com.example.vhomestay.enums.Provider;
import com.example.vhomestay.enums.TokenType;
import com.example.vhomestay.model.dto.request.*;
import com.example.vhomestay.model.dto.response.UserResponseDto;
import com.example.vhomestay.model.entity.*;
import com.example.vhomestay.repository.*;
import com.example.vhomestay.security.SecurityUtil;
import com.example.vhomestay.service.*;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import com.example.vhomestay.model.dto.response.LoginResponseDto;
import com.example.vhomestay.security.TokenProvider;
import com.example.vhomestay.util.exception.ResourceForbiddenException;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl extends BaseServiceImpl<Account, Long, AccountRepository> implements AccountService {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final GenerateRandomNumber passwordService;
    private final ManagerRepository managerRepository;
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final CustomerService customerService;
    private final ManagerService managerService;
    private final AdminService adminService;
    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;

    @Override
    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public boolean register(RegisterRequestDto request) throws MessagingException {
        Account account = new Account();
        account.setEmail(request.getEmail());
        account.setStatus(AccountStatus.INACTIVE);
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setRole(AccountRole.CUSTOMER);

        Optional<Account> accountOptional = accountRepository.findByEmailIgnoreCase(request.getEmail());
        if (accountOptional.isPresent()) {
            throw new ResourceBadRequestException("email.exists");
        }
        accountRepository.save(account);

        Token newToken = new Token();
        newToken.setToken(passwordService.generateRandomTokenToVerifyEmail());
        newToken.setUser(account);
        newToken.setCreatedDate(LocalDateTime.now());
        tokenRepository.save(newToken);

        String firstName = UriUtils.encode(request.getFirstName(), StandardCharsets.UTF_8);
        String lastName = UriUtils.encode(request.getLastName(), StandardCharsets.UTF_8);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(account.getEmail());
        helper.setSubject("[Làng H'Mông Pả Vi] Xác thực tài khoản");
        Context context = new Context();
        String link = DeployConfig.DOMAIN + "/auth/confirm-account?token=" + newToken.getToken() + "&firstName=" + firstName + "&lastName=" + lastName;
        context.setVariable("link", link);
        String html = templateEngine.process("email-verify-email", context);
        helper.setText(html, true);
        emailSender.send(message);
        return true;
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(request.getEmail());
        if (optionalAccount.isEmpty()) {
            throw new ResourceNotFoundException("user.not.registered");
        }
        if (optionalAccount.get().getProvider() != null){
            throw new ResourceBadRequestException("account.social.login.cannot.login");
        }
        UserResponseDto userResponseDto;
        if (optionalAccount.get().getRole().equals(AccountRole.CUSTOMER)) {
            Optional<Customer> optionalCustomer = customerRepository.getCustomerByAccountEmail(request.getEmail());
            userResponseDto = customerService.mapToDTO(optionalCustomer.get());
        } else if (optionalAccount.get().getRole().equals(AccountRole.MANAGER)) {
            Optional<Manager> optionalManager = managerRepository.findByAccountEmail(request.getEmail());
            userResponseDto = managerService.mapToDTO(optionalManager.get());
        } else {
            Optional<Admin> optionalAdmin = adminRepository.findByAccountEmail(request.getEmail());
            userResponseDto = adminService.mapToDTO(optionalAdmin.get());
        }
        if (!new BCryptPasswordEncoder().matches(request.getPassword(), optionalAccount.get().getPassword())) {
            throw new ResourceNotFoundException("account.login.failed");
        }
        if (optionalAccount.get().getStatus() != AccountStatus.ACTIVE) {
            throw new ResourceForbiddenException("account.not.activate");
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);;
        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);
        revokeToken(optionalAccount.get());
        saveUserToken(optionalAccount.get(), accessToken);

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userResponseDto)
                .build();
    }

    private void revokeToken(Account account) {
        Optional<Token> optionalToken = tokenRepository.deleteAllByUserEmail(account.getEmail());
        if (optionalToken.isPresent()){
            tokenRepository.delete(optionalToken.get());
        }
    }

    private void saveUserToken(Account account, String refreshToken) {
        Token token = new Token();
        token.setUser(account);
        token.setToken(refreshToken);
        token.setTokenType(TokenType.REFRESH);
        token.setExpired(false);
        token.setRevoked(false);
        tokenRepository.save(token);
    }

    public LoginResponseDto refreshToken(String refreshToken) {
        if (refreshToken == null) {
            return null;
        }

        Authentication authentication = tokenProvider.getAuthentication(refreshToken);
        Optional<Account> account = accountRepository.findByEmail(authentication.getName());
        if (authentication != null && account.isPresent()) {
            if (tokenProvider.isTokenValidate(refreshToken, authentication)) {
                String accessToken = tokenProvider.generateToken(authentication);
                return LoginResponseDto.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }
        return null;
    }

    @Override
    public boolean resetPassword(ResetPasswordRequestDto request) throws MessagingException {
        String tempPassword = passwordService.generateRandomPassword();
        String otp = passwordService.generateOTP();
        Optional<Account> optionalAccount = accountRepository.findByEmail(request.getEmail());
        if (optionalAccount.get().getProvider() != null){
            throw new ResourceBadRequestException("account.social.login.can.not.change.or.forget.password");
        }
        if (optionalAccount.isEmpty()) {
            return false;
        }
        Account account = optionalAccount.get();
        account.setPassword(passwordEncoder.encode(tempPassword));
        account.setOtp(otp);
        account.setOtpCreationTime(LocalDateTime.now());
        accountRepository.save(account);

        MimeMessage msg = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setTo(account.getEmail());
        helper.setSubject("[Làng H'Mông Pả Vi] Mã xác thực OTP");
        Context context = new Context();
        context.setVariable("otp", otp);
        String html = templateEngine.process("email-verify-otp", context);
        helper.setText(html, true);
        emailSender.send(msg);

        return true;
    }

    @Override
    public String verifyOTPForResetPassword(Account account) {
        LocalDateTime otpCreateTime = account.getOtpCreationTime();
        if (otpCreateTime.plusMinutes(15).isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException(messageSource.getMessage("account.otp.hasExpired", null, Locale.getDefault()));
        }
        return account.getEmail();
    }

    @Override
    public Optional<Account> findByOtp(String otp) {
        Optional<Account> optionalAccount = accountRepository.findByOtp(otp);
        if (optionalAccount.isPresent()) {
            return optionalAccount;
        } else {
            throw new ResourceNotFoundException(messageSource.getMessage("account.otp.notValid", null, Locale.getDefault()));
        }
    }

    @Override
    public boolean changePassword(ChangePasswordRequestDto request, String email) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(email);
        if (optionalAccount.get().getProvider() != null){
            throw new ResourceBadRequestException("account.social.login.can.not.change.or.forget.password");
        }
        if (optionalAccount.isPresent() && new BCryptPasswordEncoder().matches(request.getOldPassword(), optionalAccount.get().getPassword())) {
            if (request.getNewPassword().equals(request.getConfirmPassword())) {
                optionalAccount.get().setPassword(passwordEncoder.encode(request.getNewPassword()));
                accountRepository.save(optionalAccount.get());
                return true;
            } else {
                throw new ResourceBadRequestException("password.change.error.confirmPassword");
            }
        }
        return false;
    }

    @Override
    public boolean changePasswordForResetPassword(ChangePasswordForResetRequestDto request) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(request.getEmail());
        if (optionalAccount.isPresent() && request.getNewPassword().equals(request.getConfirmPassword())) {
            optionalAccount.get().setPassword(passwordEncoder.encode(request.getNewPassword()));
            optionalAccount.get().setOtp(null);
            accountRepository.save(optionalAccount.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean confirmEmail(String confirmationToken, String firstName, String lastName) {
        Token token = tokenRepository.findByToken(confirmationToken).orElseThrow(() -> new ResourceNotFoundException("token.notFound"));
        if (token == null) {
            throw new ResourceBadRequestException("token.notFound");
        } else if (token.getCreatedDate().plusMinutes(15).isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            throw new ResourceBadRequestException("token.hasExpired");
        } else if (token != null) {
            Account account = accountRepository.findAccountByEmail(token.getUserEntity().getEmail()).get();
            account.setStatus(AccountStatus.ACTIVE);
            AccountRole accountRole = account.getRole();
            if (accountRole.equals(AccountRole.CUSTOMER)) {
                Customer customer = new Customer();
                String firstNameDecoded = java.net.URLDecoder.decode(firstName, StandardCharsets.UTF_8).trim();
                String lastNameDecoded = java.net.URLDecoder.decode(lastName, StandardCharsets.UTF_8).trim();
                customer.setFirstName(firstNameDecoded);
                customer.setLastName(lastNameDecoded);
                customer.setAccount(account);
                account.setCustomer(customer);
            }
            accountRepository.save(account);
            tokenRepository.delete(token);
            return true;
        }
        return false;
    }


    @Override
    public boolean reSendConfirmEmail(String email, String firstName, String lastName) throws MessagingException {
        Optional<Account> optionalAccount = accountRepository.findAccountByEmail(email);

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            Token newToken = tokenRepository.findByUserId(optionalAccount.get().getId()).get();
            newToken.setToken(passwordService.generateRandomTokenToVerifyEmail());
            newToken.setUser(account);
            newToken.setCreatedDate(LocalDateTime.now());
            tokenRepository.save(newToken);

            String first = UriUtils.encode(firstName, StandardCharsets.UTF_8);
            String last = UriUtils.encode(lastName, StandardCharsets.UTF_8);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(account.getEmail());
            helper.setSubject("[Làng H'Mông Pả Vi] Xác thực tài khoản");
            Context context = new Context();
            String link = DeployConfig.DOMAIN + "/auth/confirm-account?token=" + newToken.getToken() + "&firstName=" + first + "&lastName=" + last;
            context.setVariable("link", link);
            String html = templateEngine.process("email-verify-email", context);
            helper.setText(html, true);
            emailSender.send(message);
            return true;
        }
        return false;
    }

    @Override
    public LoginResponseDto socialLogin(RegisterWithoutVerifyRequest request) {
        Optional<Account> account = findByEmail(request.getEmail());
        if (account.isPresent()) {
            if (!account.get().getStatus().equals(AccountStatus.ACTIVE)) {
                throw new ResourceForbiddenException("account.not.activate");
            }
            if (account.get().getProvider() == null || !account.get().getProvider().equals(Provider.GOOGLE)){
                throw new ResourceBadRequestException(messageSource.getMessage("account.social.login.failed", null ,Locale.getDefault()));
            }
            Optional<Customer> optionalCustomer = customerRepository.getCustomerByAccountEmail(request.getEmail());
            UserResponseDto userResponseDto = customerService.mapToDTO(optionalCustomer.get());

            String token = tokenProvider.generateToken(account.get());
            String refreshToken = tokenProvider.generateRefreshToken(account.get());
            saveUserToken(account.get(), refreshToken);
            return new LoginResponseDto(token, refreshToken, userResponseDto);
        }
        return registerSocialAccount(request);
    }

    private LoginResponseDto registerSocialAccount(RegisterWithoutVerifyRequest request) {
        Account account = new Account();
        account.setEmail(request.getEmail());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setRole(AccountRole.CUSTOMER);
        account.setStatus(AccountStatus.ACTIVE);
        account.setProvider(Provider.GOOGLE);
        Customer customer = new Customer();
        customer.setAvatar(request.getAvatar());
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        account.setCustomer(customer);
        customer.setAccount(account);
        try {
            accountRepository.save(account);
            String token = tokenProvider.generateToken(account);
            String refreshToken = tokenProvider.generateRefreshToken(account);
            saveUserToken(account, refreshToken);
            Optional<Customer> optionalCustomer = customerRepository.getCustomerByAccountEmail(request.getEmail());
            UserResponseDto userResponseDto = customerService.mapToDTO(optionalCustomer.get());
            return new LoginResponseDto(token, refreshToken, userResponseDto);
        } catch (Exception e) {
            throw new ResourceNotFoundException("user.not.registered");
        }
    }
}

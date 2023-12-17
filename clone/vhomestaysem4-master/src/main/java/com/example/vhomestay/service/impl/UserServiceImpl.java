package com.example.vhomestay.service.impl;

import com.example.vhomestay.config.DeployConfig;
import com.example.vhomestay.constant.DateTimeConstant;
import com.example.vhomestay.enums.AccountRole;
import com.example.vhomestay.enums.AccountStatus;
import com.example.vhomestay.enums.HouseholdStatus;
import com.example.vhomestay.model.dto.request.user.UserCreateRequestDto;
import com.example.vhomestay.model.dto.response.booking.customer.HouseholdNameDto;
import com.example.vhomestay.model.dto.response.user.*;
import com.example.vhomestay.model.entity.*;
import com.example.vhomestay.repository.*;
import com.example.vhomestay.security.SecurityUtil;
import com.example.vhomestay.service.*;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import com.example.vhomestay.util.exception.ResourceInternalServerErrorException;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl extends BaseServiceImpl<Account, Long, AccountRepository> implements UserService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final ManagerRepository managerRepository;
    private final AdminRepository adminRepository;
    private final HouseholdRepository householdRepository;
    private final BookingRepository bookingRepository;
    private final MessageSource messageSource;
    private final GenerateRandomNumberServiceImpl generateRandomNumberService;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;



    @Override
    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public UserForAdminResponseDto getUsersByAdmin() {
        Optional<AccountRole> role = SecurityUtil.getRoleCurrentUserLogin();
        List<UserInfoResponseDto> allUsers = new ArrayList<>();
        List<UserInfoResponseDto> admins = adminRepository.findAllByAdmin();
        List<UserInfoResponseDto> managers = managerRepository.findAllByAdmin();
        List<UserInfoResponseDto> customers = customerRepository.findAllByAdmin();
        if (role.isPresent()) {
            if (!role.get().equals(AccountRole.ADMIN)) {
                allUsers.addAll(admins);
            }
            allUsers.addAll(managers);
            allUsers.addAll(customers);
        }
        List<HouseholdNameDto> householdList = householdRepository.findAllHouseholdNameHaveNotManager();
        return new UserForAdminResponseDto(admins.size(), managers.size(), customers.size(), allUsers, householdList);
    }

    @Override
    public void inactiveOrActiveAccountOfUserByAdmin(Long accountId) {
        try {
            Optional<Account> optionalAccount = accountRepository.findById(accountId);
            if (optionalAccount.isEmpty() || optionalAccount.get().getStatus().equals(AccountStatus.DELETED)) {
                throw new ResourceBadRequestException(messageSource.getMessage("account.not.found.or.permission.denied", null, Locale.getDefault()));
            }
            Account account = optionalAccount.get();
            if (account.getStatus().equals(AccountStatus.INACTIVE)) {
                account.setStatus(AccountStatus.ACTIVE);
            } else {
                account.setStatus(AccountStatus.INACTIVE);
            }
            accountRepository.save(account);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException(messageSource.getMessage("account.update.status.error", null, Locale.getDefault()));
        }
    }

    @Override
    public void deleteUserByAdmin(Long accountId) {
        try {
            Optional<Account> optionalAccount = accountRepository.findById(accountId);
            if (optionalAccount.isEmpty() || optionalAccount.get().getStatus().equals(AccountStatus.DELETED)) {
                throw new ResourceBadRequestException(messageSource.getMessage("account.not.found.or.permission.denied", null, Locale.getDefault()));
            }
            Account account = optionalAccount.get();
            account.setStatus(AccountStatus.DELETED);
            accountRepository.save(account);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException(messageSource.getMessage("account.delete.error", null, Locale.getDefault()));
        }
    }

    @Override
    public UserDetailResponseDto getUserDetailByAdmin(Long accountId) {
        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        Account account = optionalAccount
                .filter(a -> !a.getStatus().equals(AccountStatus.DELETED))
                .orElseThrow(() -> new ResourceBadRequestException(messageSource.getMessage("account.not.found.or.permission.denied", null, Locale.getDefault())));
        Optional<AccountRole> optionalRoleCurrentUserLogin = SecurityUtil.getRoleCurrentUserLogin();
        AccountRole roleCurrentUserLogin = optionalRoleCurrentUserLogin.get();

        AccountRole accountRole = account.getRole();
        if ((accountRole.equals(AccountRole.ADMIN) || accountRole.equals(AccountRole.SUPER_ADMIN)) && roleCurrentUserLogin.equals(AccountRole.ADMIN)) {
            throw new ResourceBadRequestException(messageSource.getMessage("account.not.found.or.permission.denied", null, Locale.getDefault()));
        }

        UserDetailResponseDto userDetail = new UserDetailResponseDto();
        userDetail.setRole(accountRole);
        userDetail.setStatus(account.getStatus());
        userDetail.setEmail(account.getEmail());
        switch (accountRole) {
            case ADMIN, SUPER_ADMIN -> {
                userDetail.setAvatar(account.getAdmin().getAvatar());
                userDetail.setFirstName(account.getAdmin().getFirstName());
                userDetail.setLastName(account.getAdmin().getLastName());
                userDetail.setPhoneNumber(account.getAdmin().getPhoneNumber());
                userDetail.setAddress(account.getAdmin().getAddress());
                userDetail.setDateOfBirth(account.getAdmin().getDateOfBirth());
                userDetail.setGender(account.getAdmin().getGender());
            }
            case MANAGER -> {
                userDetail.setAvatar(account.getManager().getAvatar());
                userDetail.setFirstName(account.getManager().getFirstName());
                userDetail.setLastName(account.getManager().getLastName());
                userDetail.setPhoneNumber(account.getManager().getPhoneNumber());
                userDetail.setAddress(account.getManager().getAddress());
                userDetail.setDateOfBirth(account.getManager().getDateOfBirth());
                userDetail.setGender(account.getManager().getGender());
            }
            case CUSTOMER -> {
                userDetail.setAvatar(account.getCustomer().getAvatar());
                userDetail.setFirstName(account.getCustomer().getFirstName());
                userDetail.setLastName(account.getCustomer().getLastName());
                userDetail.setPhoneNumber(account.getCustomer().getPhoneNumber());
                userDetail.setAddress(account.getCustomer().getAddress());
                userDetail.setDateOfBirth(account.getCustomer().getDateOfBirth());
                userDetail.setGender(account.getCustomer().getGender());
            }
        }
        return userDetail;
    }

    @Override
    public List<BookingOfCustomerDto> getBookingOfCustomerByAdmin(Long accountId) {
        return bookingRepository.getBookingOfCustomer(accountId);
    }

    @Override
    public boolean checkEmailExist(String email) {
        return accountRepository.findByEmailIgnoreCase(email).isPresent();
    }

    @Override
    public boolean checkPhoneNumberExits(String phoneNumber) {
        return accountRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    @Override
    public void createUser(UserCreateRequestDto userCreateRequestDto) {
        try {
            Optional<AccountRole> optionalRoleCurrentUserLogin = SecurityUtil.getRoleCurrentUserLogin();
            AccountRole roleCurrentUserLogin = optionalRoleCurrentUserLogin.get();
            if (roleCurrentUserLogin.equals(AccountRole.ADMIN) && userCreateRequestDto.getRole().equals(AccountRole.ADMIN)) {
                throw new ResourceBadRequestException(messageSource.getMessage("account.permission.denied", null, Locale.getDefault()));
            }
            Account account = new Account();
            String password = generateRandomNumberService.generateRandomPassword();
            account.setEmail(userCreateRequestDto.getEmail());
            account.setPassword(passwordEncoder.encode(password));
            account.setStatus(AccountStatus.INACTIVE);
            account.setRole(userCreateRequestDto.getRole());
            accountRepository.save(account);
            String vietnameseRole;
            if (userCreateRequestDto.getRole().equals(AccountRole.ADMIN)) {
                vietnameseRole = "Quản trị viên";
                Admin admin = new Admin();
                admin.setFirstName(userCreateRequestDto.getFirstName());
                admin.setLastName(userCreateRequestDto.getLastName());
                admin.setPhoneNumber(userCreateRequestDto.getPhoneNumber());
                admin.setAccount(account);
                adminRepository.save(admin);
            } else {
                vietnameseRole = "Quản lý hộ kinh doanh";
                Manager manager = new Manager();
                manager.setFirstName(userCreateRequestDto.getFirstName());
                manager.setLastName(userCreateRequestDto.getLastName());
                manager.setPhoneNumber(userCreateRequestDto.getPhoneNumber());
                manager.setAccount(account);
                managerRepository.save(manager);
                Household household = householdRepository.findById(userCreateRequestDto.getHouseholdId())
                        .filter(h -> !h.getStatus().equals(HouseholdStatus.DELETED))
                        .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("household.not.found", null, Locale.getDefault())));
                household.setManager(manager);
                householdRepository.save(household);
            }

            Token newToken = new Token();
            newToken.setToken(generateRandomNumberService.generateRandomTokenToVerifyEmail());
            newToken.setUser(account);
            newToken.setCreatedDate(LocalDateTime.now());
            tokenRepository.save(newToken);

            String firstName = UriUtils.encode(userCreateRequestDto.getFirstName(), StandardCharsets.UTF_8);
            String lastName = UriUtils.encode(userCreateRequestDto.getLastName(), StandardCharsets.UTF_8);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(account.getEmail());
            helper.setSubject("[Làng H'Mông Pả Vi] Lời mời làm " + vietnameseRole + " đã được gửi đến bạn");
            Context context = new Context();
            String link = DeployConfig.DOMAIN + "/auth/confirm-account?token=" + newToken.getToken() + "&firstName=" + firstName + "&lastName=" + lastName;
            context.setVariable("linkVerify", link);
            context.setVariable("role", vietnameseRole);
            context.setVariable("email", account.getEmail());
            context.setVariable("password", password);
            String html = templateEngine.process("email-verify-email-for-manager-and-admin", context);
            helper.setText(html, true);
            emailSender.send(message);
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException(messageSource.getMessage("internal.server.error", null, Locale.getDefault()));
        }
    }

}

package com.example.vhomestay.security;

import com.example.vhomestay.constant.AppConstant;
import com.example.vhomestay.enums.AccountRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public final class SecurityUtil {

    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Object object = authentication.getPrincipal();
        if (object instanceof UserDetails) {
            return Optional.of(((UserDetails) object).getUsername());
        }
        return Optional.empty();
    }

    public static Optional<AccountRole> getRoleCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        return authentication.getAuthorities().stream()
                .map(authority -> AccountRole.valueOf(authority.getAuthority().replaceAll(AppConstant.USER_ROLE_PREFIX, "")))
                .findFirst();
    }

    public static boolean isAdmin() {
        Optional<AccountRole> userRoleOptional = getRoleCurrentUserLogin();
        return userRoleOptional.isPresent() && userRoleOptional.get() == AccountRole.ADMIN;
    }

    public static boolean isCustomer() {
        Optional<AccountRole> userRoleOptional = getRoleCurrentUserLogin();
        return userRoleOptional.isPresent() && userRoleOptional.get() == AccountRole.CUSTOMER;
    }

    public static boolean isManager() {
        Optional<AccountRole> userRoleOptional = getRoleCurrentUserLogin();
        return userRoleOptional.isPresent() && userRoleOptional.get() == AccountRole.MANAGER;
    }

    public static boolean isSuperAdmin() {
        Optional<AccountRole> userRoleOptional = getRoleCurrentUserLogin();
        return userRoleOptional.isPresent() && userRoleOptional.get() == AccountRole.SUPER_ADMIN;
    }

}

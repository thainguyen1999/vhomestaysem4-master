package com.example.vhomestay.security;

import com.example.vhomestay.constant.AppConstant;
import com.example.vhomestay.model.entity.Account;
import com.example.vhomestay.service.impl.UserServiceImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserServiceImpl userService;

    public UserDetailsServiceImpl(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        Optional<Account> accountOptional = userService.findByEmail(email);
        if (accountOptional.isEmpty()) {
            throw new UsernameNotFoundException("Email: \"" + email + "\" is not exist in the system");
        }
        Account account = accountOptional.get();
        List<GrantedAuthority> roles = Collections.singletonList(
                new SimpleGrantedAuthority(AppConstant.USER_ROLE_PREFIX + account.getRole().name()) // ROLE_ADMIN, ROLE_USER
        );

        return new User(account.getEmail(), account.getPassword(), roles);
    }
}

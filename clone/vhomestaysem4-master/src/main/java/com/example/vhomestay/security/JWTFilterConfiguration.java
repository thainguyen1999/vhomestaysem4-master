package com.example.vhomestay.security;

import com.example.vhomestay.repository.TokenRepository;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JWTFilterConfiguration
        extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;
    private final TokenRepository tokenRepository;

    public JWTFilterConfiguration(TokenProvider tokenProvider, TokenRepository tokenRepository) {
        this.tokenProvider = tokenProvider;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        JWTFilter filter = new JWTFilter(tokenProvider, tokenRepository);
        builder.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}

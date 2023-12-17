package com.example.vhomestay.security;

import com.example.vhomestay.enums.AccountRole;
import com.example.vhomestay.repository.TokenRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final TokenProvider tokenProvider;
    private final TokenRepository tokenRepository;

    public SecurityConfiguration(TokenProvider tokenProvider, TokenRepository tokenRepository) {
        this.tokenProvider = tokenProvider;
        this.tokenRepository = tokenRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .exceptionHandling().authenticationEntryPoint(new AuthEntryPointJwt()).and()
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/user/**").permitAll()
                        .requestMatchers("/unread-notification/**").permitAll()
                        .requestMatchers("/api/v*/private/**").permitAll()
                        .requestMatchers("/api/v*/booking/**").permitAll()
                        .requestMatchers("/api/v*/news/**").permitAll()
                        .requestMatchers("/api/v*/gallery/**").permitAll()
                        .requestMatchers("/api/v*/home/**").permitAll()
                        .requestMatchers("/api/v*/auth/**").permitAll()
                        .requestMatchers(("/api/v*/village/**")).permitAll()
                        .requestMatchers(("/api/v*/customer/service/**")).permitAll()
                        .requestMatchers("/api/v*/customer/household/**").permitAll()
                        .requestMatchers("/api/v*/customer/local-product/**").permitAll()
                        .requestMatchers("/api/v*/customers/**").hasAnyRole(AccountRole.CUSTOMER.name(), AccountRole.MANAGER.name(), AccountRole.ADMIN.name())
                        .requestMatchers("/api/v*/manager/**").hasRole(AccountRole.MANAGER.name())
                        .requestMatchers("api/v*/admin/**").hasAnyRole(AccountRole.ADMIN.name(), AccountRole.SUPER_ADMIN.name())
                        .anyRequest().authenticated()
                )
                .httpBasic()
                .and()
                .apply(new JWTFilterConfiguration(tokenProvider, tokenRepository))
                .and()
                .logout()
                .logoutUrl("/api/v1/auth/logout")
                .addLogoutHandler(new JwtLogoutHandler(tokenRepository, tokenProvider))
                .logoutSuccessHandler(
                        (request, response, authentication) ->
                                SecurityContextHolder.clearContext()
                )
        ;
        return http.build();
    }
}

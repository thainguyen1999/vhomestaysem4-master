package com.example.vhomestay.config;

import com.example.vhomestay.security.SecurityUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AppAuditorAware implements AuditorAware<String> {
    @Override
    public @NotNull Optional<String> getCurrentAuditor() {
        return Optional.of(SecurityUtil.getCurrentUserLogin().orElse("Anonymous"));
    }

}

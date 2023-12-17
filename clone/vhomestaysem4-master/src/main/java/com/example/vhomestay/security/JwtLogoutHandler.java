package com.example.vhomestay.security;

import com.example.vhomestay.model.entity.Token;
import com.example.vhomestay.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final TokenProvider tokenProvider;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        String bearerToken = request.getHeader("Authorization");
        String jwtToken = null;
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            jwtToken = bearerToken.substring(7);
        }
        Authentication auth = tokenProvider.getAuthentication(jwtToken);
        if (auth.getName() == null) {
            return;
        }
        Optional<Token> storedToken = tokenRepository.findUserByEmail(auth.getName());
        Token token = null;
        if (storedToken.isPresent()) {
            token = storedToken.get();
            token.setExpired(true);
            token.setRevoked(true);
            tokenRepository.delete(token);
        }
    }
}

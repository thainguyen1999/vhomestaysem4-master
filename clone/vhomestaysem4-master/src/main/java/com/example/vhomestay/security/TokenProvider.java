package com.example.vhomestay.security;

import com.example.vhomestay.model.entity.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    @Value("${security.jwt.secret-key}")
    private String SECRET_KEY;
    @Value("${security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${security.jwt.refresh-token.expiration}")
    private long refreshExpiration;
    @Value("${security.jwt.token.expiration}")
    private long tokenExpiration;

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private String buildToken(Map<String, Object> extraClaims, Authentication authentication, long expiration) {
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .claim("authorities", roles)
                .setSubject(authentication.getName())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String buildToken(Map<String, Object> extraClaims, Account account, long expiration) {
        String roles = "ROLE_" + account.getRole().toString();
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .claim("authorities", roles)
                .setSubject(account.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(Account account) {
        return buildToken(new HashMap<>(), account, jwtExpiration);
    }


    public String generateToken(Authentication authentication) {
        return generateToken(new HashMap<>(), authentication);
    }

    public String generateToken(Map<String, Object> extraClaims, Authentication authentication) {
        return buildToken(extraClaims, authentication, jwtExpiration);
    }

    public String generateRefreshToken(Authentication authentication) {
        return buildToken(new HashMap<>(), authentication, refreshExpiration);
    }

    public String generateRefreshToken(Account account) {
        return buildToken(new HashMap<>(), account, refreshExpiration);
    }

    public boolean isTokenValidate(String token, Authentication authentication) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        String userEmail = extractUsername(token);

        return (userEmail.equals(authentication.getName()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Authentication getAuthentication(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        try {
            Claims claims = extractAllClaims(token);
            List<GrantedAuthority> authorityList = Arrays.stream(claims.get("authorities")
                            .toString().split(","))
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            User principal = new User(claims.getSubject(), "", authorityList);
            return new UsernamePasswordAuthenticationToken(principal, token, authorityList);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }


}

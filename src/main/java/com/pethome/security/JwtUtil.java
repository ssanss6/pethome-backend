package com.pethome.security;

import com.pethome.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * JWT 工具
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties properties;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT
     *
     * @param user 登录主体
     * @return token 字符串
     */
    public String generate(LoginUser user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + properties.getExpireMinutes() * 60_000L);
        return Jwts.builder()
                .subject(String.valueOf(user.getUserId()))
                .claim("userType", user.getUserType())
                .claim("roles", user.getRoles())
                .claim("openid", user.getOpenid())
                .claim("username", user.getUsername())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    /**
     * 解析 JWT，还原登录主体
     *
     * @param token token 字符串
     * @return 登录主体
     */
    @SuppressWarnings("unchecked")
    public LoginUser parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        Long userId = Long.valueOf(claims.getSubject());
        Integer userType = claims.get("userType", Integer.class);
        List<String> roles = claims.get("roles", List.class);
        String openid = claims.get("openid", String.class);
        String username = claims.get("username", String.class);
        return new LoginUser(userId, userType, roles, openid, username);
    }
}

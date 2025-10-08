package com.example.framework.service.impl;

import com.example.framework.model.dto.UserDetailsDTO;
import com.example.framework.service.RedisService;
import com.example.framework.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static com.example.framework.constant.AuthConstant.*;
import static com.example.framework.constant.RedisConstant.LOGIN_USER;

/**
 * @author: qinglong
 * @create-date: 2023/10/8 16:34
 */
@Service
public class TokenServiceImpl implements TokenService {
    @Autowired
    private RedisService redisService;

    @Override
    public String createToken(UserDetailsDTO userDetailsDTO) {
        refreshToken(userDetailsDTO);
        String userId = userDetailsDTO.getId().toString();
        return createToken(userId);
    }

    @Override
    public String createToken(String subject) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        return Jwts.builder().setId(getUuid()).setSubject(subject)
                .setIssuer("olivesQ")
                .signWith(secretKey, signatureAlgorithm).compact();
    }

    @Override
    public void refreshToken(UserDetailsDTO userDetailsDTO) {
        LocalDateTime currentTime = LocalDateTime.now();
        userDetailsDTO.setExpireTime(currentTime.plusSeconds(EXPIRE_TIME));
        String userId = userDetailsDTO.getId().toString();
        redisService.hSet(LOGIN_USER, userId, userDetailsDTO, EXPIRE_TIME);
    }

    @Override
    public void renewToken(UserDetailsDTO userDetailsDTO) {
        LocalDateTime expireTime = userDetailsDTO.getExpireTime();
        LocalDateTime currentTime = LocalDateTime.now();
        if (Duration.between(currentTime, expireTime).toMinutes() <= TWENTY_MINUTES) {
            refreshToken(userDetailsDTO);
        }
    }

    @Override
    public Claims parseToken(String token) {
        SecretKey secretKey = generalKey();
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public UserDetailsDTO getUserDetailDTO(HttpServletRequest request) {
        String token = Optional.ofNullable(request.getHeader(TOKEN_HEADER)).orElse("").replaceFirst(TOKEN_PREFIX, "");
        if (StringUtils.hasText(token) && !token.equals("null")) {
            Claims claims = parseToken(token);
            String userId = claims.getSubject();
            return (UserDetailsDTO) redisService.hGet(LOGIN_USER, userId);
        }
        return null;
    }

    @Override
    public void delLoginUser(Integer userId) {
        redisService.hDel(LOGIN_USER, String.valueOf(userId));
    }

    public String getUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public SecretKey generalKey() {
        // 使用足够长的安全字符串
        String secretString = "your-very-secure-secret-key-with-at-least-32-characters-here";
        return Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));

    }

}

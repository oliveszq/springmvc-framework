package com.example.framework.filter;


import com.example.framework.model.dto.UserDetailsDTO;
import com.example.framework.service.TokenService;
import com.example.framework.utils.UserUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Objects;

@Component
@SuppressWarnings("all")
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    public TokenService tokenService;

    @Autowired
    public AuthenticationEntryPoint authenticationEntryPoint;

    /**
     * 前置过滤器
     * @param request
     * @param response
     * @param filterChain
     */
    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        UserDetailsDTO userDetailsDTO = tokenService.getUserDetailDTO(request);
        if (Objects.nonNull(userDetailsDTO) && Objects.isNull(UserUtil.getAuthentication())) {
            tokenService.renewToken(userDetailsDTO);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetailsDTO, null, userDetailsDTO.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }
}

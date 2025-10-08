package com.example.framework.config;

import com.example.framework.filter.JwtAuthenticationTokenFilter;
import com.example.framework.handler.CustomAuthorizationManagerImpl;
import com.example.framework.handler.FilterInvocationSecurityMetadataSourceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author: qinglong
 * @create-date: 2023/10/8 9:16
 */
@Configuration
public class WebSecurityConfig {

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Bean
    public FilterInvocationSecurityMetadataSource securityMetadataSource() {
        return new FilterInvocationSecurityMetadataSourceImpl();
    }

    @Bean
    public AuthorizationManager customAuthorizationManager() {
        return new CustomAuthorizationManagerImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin((formLogin) ->
                formLogin.loginProcessingUrl("/users/login")
                        .successHandler(authenticationSuccessHandler)
                        .failureHandler(authenticationFailureHandler));
        //对url请求进行授权访问
        http.authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers("/doc.html").permitAll()
                                .requestMatchers("/webjars/**").permitAll()
                                .requestMatchers("/webjars/**").permitAll()
                                .requestMatchers("/swagger-resources/**").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/v3/api-docs").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .anyRequest().access(customAuthorizationManager()))
                //关闭csrf（跨域请求伪造）保护功能
                .csrf((csrf) -> csrf.disable())
                .exceptionHandling((exceptionHandling) ->
                        exceptionHandling//设置身份验证入口点和异常处理器
                                .authenticationEntryPoint(authenticationEntryPoint)
                                //访问被拒绝时的处理器
                                .accessDeniedHandler(accessDeniedHandler))

                //设置session为无状态，即不需要session来保存用户信息
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //添加前置过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

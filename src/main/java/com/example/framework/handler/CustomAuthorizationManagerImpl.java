package com.example.framework.handler;

import com.example.framework.mapper.RoleMapper;
import com.example.framework.model.dto.ResourceRoleDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
@Slf4j
@Component
public class CustomAuthorizationManagerImpl implements AuthorizationManager<RequestAuthorizationContext> {

    @Autowired
    private RoleMapper roleMapper;

    // 存储资源与角色的映射关系（同原resourceRoleList）
    private static List<ResourceRoleDTO> resourceRoleList;

    // 初始化时加载资源角色列表（同原loadResourceRoleList）
    @PostConstruct
    private void loadResourceRoleList() {
        resourceRoleList = roleMapper.listResourceRoles();
    }

    // 清除缓存，用于动态刷新资源权限（同原clearDataSource）
    public void clearDataSource() {
        resourceRoleList = null;
    }

    /**
     * 核心授权方法：替代原SecurityMetadataSource的getAttributes + AccessDecisionManager的决策逻辑
     * 1. 获取当前请求的URL和方法
     * 2. 匹配对应的资源所需角色
     * 3. 检查当前用户是否拥有所需角色
     */
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        // 1. 加载资源角色列表（如果为空则重新加载）
        if (CollectionUtils.isEmpty(resourceRoleList)) {
            this.loadResourceRoleList();
        }

        // 2. 获取当前请求的URL和方法（替代原FilterInvocation的信息获取）
        String requestUrl = context.getRequest().getRequestURI();
        String requestMethod = context.getRequest().getMethod();

        // 3. 匹配资源对应的角色（同原getAttributes的核心逻辑）
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        List<String> requiredRoles = null;
        for (ResourceRoleDTO resourceRoleDTO : resourceRoleList) {
            if (antPathMatcher.match(resourceRoleDTO.getUrl(), requestUrl)
                    && resourceRoleDTO.getRequestMethod().equals(requestMethod)) {
                requiredRoles = resourceRoleDTO.getRoleList();
                break; // 匹配到第一个符合条件的资源后退出循环
            }
        }

        // 4. 处理无匹配资源的情况（根据业务决定：拒绝访问或允许访问）
        if (requiredRoles == null) {
            // 例如：未配置的资源默认访问
            return new AuthorizationDecision(true);
        }

        // 5. 处理资源需要"disable"角色的情况（同原逻辑）
        if (requiredRoles.contains("disable")) {
            return new AuthorizationDecision(false);
        }

        // 6. 检查当前用户是否拥有所需角色（替代原AccessDecisionManager的决策逻辑）
        Authentication auth = authentication.get();
        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false); // 未认证用户直接拒绝
        }

        // 提取用户拥有的角色
        Collection<? extends GrantedAuthority> userAuthorities = auth.getAuthorities();
        // 检查用户角色是否包含所需角色（至少一个匹配）
        List<String> finalRequiredRoles = requiredRoles;
        boolean hasRequiredRole = userAuthorities.stream()
                .anyMatch(authority -> finalRequiredRoles.contains(authority.getAuthority()));

        return new AuthorizationDecision(hasRequiredRole);
    }
}

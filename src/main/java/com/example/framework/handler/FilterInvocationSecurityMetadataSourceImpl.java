package com.example.framework.handler;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.example.framework.mapper.RoleMapper;
import com.example.framework.model.dto.ResourceRoleDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;

@Component
public class FilterInvocationSecurityMetadataSourceImpl implements FilterInvocationSecurityMetadataSource {

    @Autowired
    private RoleMapper roleMapper;

    private static List<ResourceRoleDTO> resourceRoleList;

    @PostConstruct
    private void loadResourceRoleList() {
        resourceRoleList = roleMapper.listResourceRoles();
    }

    public void clearDataSource() {
        resourceRoleList = null;
    }

    //权限配置类的方法，用于获取请求的资源需要的权限列表。
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        //判断权限列表是否为空，如果为空则加载权限
        if (CollectionUtils.isEmpty(resourceRoleList)) {
            this.loadResourceRoleList();
        }
        //使用FilterInvocation对象获取请求的方法和URL
        FilterInvocation fi = (FilterInvocation) object;
        String method = fi.getRequest().getMethod();
        String url = fi.getRequest().getRequestURI();

        //使用AntPathMatcher进行资源路径匹配。遍历资源角色列表，
        // 如果有一个资源角色与当前请求的URL和方法匹配成功，就获取该资源角色所需要的权限列表。
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (ResourceRoleDTO resourceRoleDTO : resourceRoleList) {
            if (antPathMatcher.match(resourceRoleDTO.getUrl(), url)
                    && resourceRoleDTO.getRequestMethod().equals(method)) {
                List<String> roleList = resourceRoleDTO.getRoleList();

                //角色列表为空，则返回一个包含"disable"权限的Collection。
                //角色列表不为空，则将角色列表转换为一个字符串数组，
                // 并使用SecurityConfig.createList()方法创建一个包含角色的SecurityConfig列表。
                if (CollectionUtils.isEmpty(roleList)) {
                    return SecurityConfig.createList("disable");
                }
                return SecurityConfig.createList(roleList.toArray(new String[]{}));
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}

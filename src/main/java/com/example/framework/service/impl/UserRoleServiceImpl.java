package com.example.framework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.framework.entity.UserRole;
import com.example.framework.mapper.UserRoleMapper;
import com.example.framework.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * @author: qinglong
 * @create-date: 2023/10/19 17:04
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
}

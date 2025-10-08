package com.example.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.framework.constant.CommonConstant;
import com.example.framework.entity.*;
import com.example.framework.exception.BizException;
import com.example.framework.handler.CustomAuthorizationManagerImpl;
import com.example.framework.mapper.*;
import com.example.framework.model.dto.PageResultDTO;
import com.example.framework.model.dto.RoleDTO;
import com.example.framework.model.dto.UserRoleDTO;
import com.example.framework.model.vo.ConditionVO;
import com.example.framework.model.vo.RoleVO;
import com.example.framework.service.RoleMenuService;
import com.example.framework.service.RoleResourceService;
import com.example.framework.service.RoleService;
import com.example.framework.utils.BeanCopyUtil;
import com.example.framework.utils.PageUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author qinglong
 * @since 2024-08-26
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private RoleResourceService roleResourceService;

    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private CustomAuthorizationManagerImpl customAuthorizationManager;
    @Override
    public List<UserRoleDTO> listUserRoles() {
        List<Role> roleList = roleMapper.selectList(new LambdaQueryWrapper<Role>()
                .select(Role::getId, Role::getRoleName));
        return BeanCopyUtil.copyList(roleList, UserRoleDTO.class);
    }

    @SneakyThrows
    @Override
    public PageResultDTO<RoleDTO> listRoles(ConditionVO conditionVO) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<Role>()
                .like(StringUtils.isNotBlank(conditionVO.getKeywords()),
                        Role::getRoleName, conditionVO.getKeywords());
        CompletableFuture<Long> asyncCount = CompletableFuture.supplyAsync(() ->
                roleMapper.selectCount(queryWrapper));
        List<RoleDTO> roleDTOs = roleMapper.listRoles(PageUtil.getLimitCurrent(),
                PageUtil.getSize(), conditionVO);
        return new PageResultDTO<>(roleDTOs, Math.toIntExact(asyncCount.get()));
    }

    @Transactional(rollbackFor = Exception.class )
    @Override
    public void saveOrUpdateRole(RoleVO roleVO) {
        Role roleCheck = roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                .select(Role::getId)
                .eq(Role::getRoleName, roleVO.getRoleName()));
        if (Objects.nonNull(roleCheck) && !(roleCheck.getId().equals(roleVO.getId()))) {
            throw new BizException("该角色存在");
        }
        Role role = Role.builder()
                .id(roleVO.getId())
                .roleName(roleVO.getRoleName())
                .isDisable(CommonConstant.FALSE)
                .build();
        this.saveOrUpdate(role);
        if (Objects.nonNull(roleVO.getResourceIds())) {
            // 删除角色资源权限
            if (Objects.nonNull(roleVO.getId())) {
                roleResourceService.remove(new LambdaQueryWrapper<RoleResource>()
                        .eq(RoleResource::getRoleId, roleVO.getId()));
            }
            List<RoleResource> roleResourceList = roleVO.getResourceIds().stream()
                    .map(resourceId -> RoleResource.builder()
                            .roleId(role.getId())
                            .resourceId(resourceId)
                            .build())
                    .collect(Collectors.toList());
            roleResourceService.saveBatch(roleResourceList);
            customAuthorizationManager.clearDataSource();
        }
        if (Objects.nonNull(roleVO.getMenuIds())) {
            if (Objects.nonNull(roleVO.getId())) {
                roleMenuService.remove(new LambdaQueryWrapper<RoleMenu>()
                        .eq(RoleMenu::getRoleId, roleVO.getId()));
            }
            List<RoleMenu> roleMenuList = roleVO.getMenuIds().stream()
                    .map(menuId -> RoleMenu.builder()
                            .roleId(role.getId())
                            .menuId(menuId)
                            .build())
                    .collect(Collectors.toList());
            roleMenuService.saveBatch(roleMenuList);
        }

    }

    @Override
    public void deleteRoles(List<Integer> roleIdList) {
        Long count = userRoleMapper.selectCount(new LambdaQueryWrapper<UserRole>()
                .in(UserRole::getRoleId, roleIdList));
        if (count > 0) {
            throw new BizException("该角色下存在用户");
        }
        roleMapper.deleteByIds(roleIdList);
    }
}

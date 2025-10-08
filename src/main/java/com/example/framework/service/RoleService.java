package com.example.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.framework.entity.Role;
import com.example.framework.model.dto.PageResultDTO;
import com.example.framework.model.dto.RoleDTO;
import com.example.framework.model.dto.UserRoleDTO;
import com.example.framework.model.vo.ConditionVO;
import com.example.framework.model.vo.RoleVO;

import java.util.List;

/**
 * @author qinglong
 * @since 2024-08-26
 */
public interface RoleService extends IService<Role> {

    List<UserRoleDTO> listUserRoles();

    PageResultDTO<RoleDTO> listRoles(ConditionVO conditionVO);

    void saveOrUpdateRole(RoleVO roleVO);

    void deleteRoles(List<Integer> roleIdList);
}

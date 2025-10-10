package com.example.framework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.framework.entity.Role;
import com.example.framework.model.dto.ResourceRoleDTO;
import com.example.framework.model.dto.RoleDTO;
import com.example.framework.model.vo.ConditionVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author qinglong
 * @since 2024-08-26
 */
@Repository
public interface RoleMapper extends BaseMapper<Role> {

    List<String> listRolesByUserInfoId(@Param("userInfoId") Integer userInfoId);

    Boolean hasSuperAdmin(@Param("userInfoId") Integer userInfoId);

    List<ResourceRoleDTO> listResourceRoles();

    List<RoleDTO> listRoles(@Param("current") Long current,
                            @Param("size") Long size,
                            @Param("conditionVO") ConditionVO conditionVO);
}

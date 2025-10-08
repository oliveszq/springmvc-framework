package com.example.framework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qinglong
 * @since 2024-08-26
 */
@Builder
@Data
@TableName("t_role_resource")
@Schema(name = "RoleResource对象", description = "角色资源表")
public class RoleResource implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "角色id")
    private Integer roleId;

    @Schema(description = "权限id")
    private Integer resourceId;


}

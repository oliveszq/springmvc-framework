package com.example.framework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 角色菜单表
 * </p>
 *
 * @author qinglong
 * @since 2024-08-26
 */
@Data
@Builder
@TableName("t_role_menu")
@Schema(name = "RoleMenu对象", description = "角色菜单表")
public class RoleMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "角色id")
    private Integer roleId;

    @Schema(description = "菜单id")
    private Integer menuId;


}

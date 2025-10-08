package com.example.framework.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_menu")
public class Menu {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;
    @Schema(description = "菜单路径")
    private String path;
    @Schema(description = "组件")
    private String component;
    @Schema(description = "菜单图标")
    private String icon;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "父id")
    private Integer parentId;
    @Schema(description = "是否隐藏")
    private Integer isHidden;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}


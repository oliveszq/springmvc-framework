package com.example.framework.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 资源表
 * </p>
 *
 * @author qinglong
 * @since 2024-08-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_resource")
@Schema(name = "Resource对象", description = "资源表")
public class Resource implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "资源名")
    private String resourceName;

    @Schema(description = "权限路径")
    private String url;

    @Schema(description = "请求方式")
    private String requestMethod;

    @Schema(description = "父模块id")
    private Integer parentId;

    @Schema(description = "是否匿名访问 0否 1是")
    private Integer isAnonymous;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;


}

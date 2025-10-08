package com.example.framework.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author qinglong
 * @since 2024-01-11
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_operation_log")
@Schema(name = "OperationLog对象", description = "")
public class OperationLog {

    private static final long serialVersionUID = 1L;

      @Schema(description = "主键id")
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @Schema(description = "操作模块")
      private String optModule;

      @Schema(description = "操作类型")
      private String optType;

      @Schema(description = "操作url")
      private String optUri;

      @Schema(description = "操作方法")
      private String optMethod;

      @Schema(description = "操作描述")
      private String optDesc;

      @Schema(description = "请求参数")
      private String requestParam;

      @Schema(description = "请求方式")
      private String requestMethod;

      @Schema(description = "返回数据")
      private String responseData;

      @Schema(description = "用户id")
      private Integer userId;

      @Schema(description = "用户昵称")
      private String nickname;

      @Schema(description = "操作ip")
      private String ipAddress;

      @Schema(description = "操作地址")
      private String ipSource;

      @Schema(description = "创建时间")
      @TableField(fill = FieldFill.INSERT)
      private LocalDateTime createTime;

      @Schema(description = "更新时间")
      @TableField(fill = FieldFill.UPDATE)
      private LocalDateTime updateTime;


}

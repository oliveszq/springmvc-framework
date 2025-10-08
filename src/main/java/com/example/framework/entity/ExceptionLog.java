package com.example.framework.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author qinglong
 * @since 2024-01-11
 */
@Getter
@Setter
@TableName("t_exception_log")
@Schema(name = "ExceptionLog对象", description = "")
public class ExceptionLog implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @Schema(description = "请求接口")
      private String optUri;

      @Schema(description = "请求方式")
      private String optMethod;

      @Schema(description = "请求方式")
      private String requestMethod;

      @Schema(description = "请求参数")
      private String requestParam;

      @Schema(description = "操作描述")
      private String optDesc;

      @Schema(description = "异常信息")
      private String exceptionInfo;

      @Schema(description = "ip")
      private String ipAddress;

      @Schema(description = "ip来源")
      private String ipSource;

      @Schema(description = "操作时间")
      @TableField( fill = FieldFill.INSERT)
      private LocalDateTime createTime;


}

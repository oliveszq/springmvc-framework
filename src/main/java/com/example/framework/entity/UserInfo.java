package com.example.framework.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author: QLong
 * @Date: 2023/3/15
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user_info")
public class UserInfo {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String email;

    private String nickname;

    private String avatar;

    @Schema(description = "用户简介")
    private String intro;

    @Schema(description = "个人网站")
    private String website;

    @Schema(description = "是否订阅")
    private Integer isSubscribe;

    @Schema(description = "是否禁用")
    private Integer isDisable;

    //配合MyMetaObjectHandler类实现自动填充功能
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}

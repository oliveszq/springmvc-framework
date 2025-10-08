package com.example.framework.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "qq登录信息")
public class QQLoginVO {

    @NotBlank(message = "openId不能为空")
    @Schema(name = "openId", description = "qq openId", required = true, type = "String")
    private String openId;

    @NotBlank(message = "accessToken不能为空")
    @Schema(name = "accessToken", description = "qq accessToken", required = true, type = "String")
    private String accessToken;

}

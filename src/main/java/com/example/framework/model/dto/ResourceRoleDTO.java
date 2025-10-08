package com.example.framework.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ResourceRoleDTO {

    private Integer id;

    private String url;

    @Schema(description = "请求的方法")
    private String requestMethod;

    @Schema(description = "角色权限")
    private List<String> roleList;

}

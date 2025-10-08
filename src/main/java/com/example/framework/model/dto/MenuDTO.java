package com.example.framework.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuDTO {

    private Integer id;

    private String name;

    private String path;

    private String component;

    private String icon;

    private LocalDateTime createTime;

    private Integer orderNum;

    private Integer isDisable;

    private Integer isHidden;
    @Schema(description = "子菜单")
    private List<MenuDTO> children;

}

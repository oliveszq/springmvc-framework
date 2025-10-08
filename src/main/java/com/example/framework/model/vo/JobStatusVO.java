package com.example.framework.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobStatusVO {

    @Schema(description="任务id" ,name = "id",required = true, type = "Integer")
    private Integer id;

    @Schema(description = "任务状态", name = "status", required = true, type = "Integer")
    private Integer status;
}

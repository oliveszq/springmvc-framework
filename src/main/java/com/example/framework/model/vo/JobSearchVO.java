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
public class JobSearchVO {

    @Schema(name= "jobName", description = "任务名称",  required = true, type = "String")
    private String jobName;

    @Schema(name = "jobGroup", description = "任务组别",  required = true, type = "String")
    private String jobGroup;

    @Schema(name = "status", description = "任务状态",  required = true, type = "Integer")
    private Integer status;
}

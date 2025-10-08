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
 * @author qinglong
 * @since 2024-08-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_job")
@Schema(name = "Job对象", description = "定时任务调度表")
public class Job implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "任务ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "任务组名")
    private String jobGroup;

    @Schema(description = "调用目标字符串")
    private String invokeTarget;

    @Schema(description = "cron执行表达式")
    private String cronExpression;

    @Schema(description = "计划执行错误策略（1立即执行 2执行一次 3放弃执行）")
    private Integer misfirePolicy;

    @Schema(description = "是否并发执行（0禁止 1允许）")
    private Integer concurrent;

    @Schema(description = "状态（0暂停 1正常）")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "备注信息")
    private String remark;


}

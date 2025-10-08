package com.example.framework.controller;


import com.example.framework.annotation.OptLog;
import com.example.framework.model.dto.JobLogDTO;
import com.example.framework.model.dto.PageResultDTO;
import com.example.framework.model.vo.JobLogSearchVO;
import com.example.framework.service.JobLogService;
import com.example.framework.utils.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.framework.constant.OptTypeConstant.DELETE;

/**
 * @author qinglong
 * @since 2024-08-27
 */
@Tag(name = "定时任务日志模块")
@RestController
public class JobLogController {
    @Autowired
    private JobLogService jobLogService;

    @Operation(summary = "获取定时任务的日志列表")
    @GetMapping("/admin/jobLogs")
    public ResultVO<PageResultDTO<JobLogDTO>> listJobLogs(JobLogSearchVO jobLogSearchVO) {
        return ResultVO.ok(jobLogService.listJobLogs(jobLogSearchVO));
    }

    @OptLog(optType = DELETE)
    @Operation(summary = "删除定时任务的日志")
    @DeleteMapping("/admin/jobLogs")
    public ResultVO<?> deleteJobLogs(@RequestBody List<Integer> ids) {
        jobLogService.deleteJobLogs(ids);
        return ResultVO.ok();
    }

    @OptLog(optType = DELETE)
    @Operation(summary = "清除定时任务的日志")
    @DeleteMapping("/admin/jobLogs/clean")
    public ResultVO<?> cleanJobLogs() {
        jobLogService.cleanJobLogs();
        return ResultVO.ok();
    }

    @Operation(summary = "获取定时任务日志的所有组名")
    @GetMapping("/admin/jobLogs/jobGroups")
    public ResultVO<?> listJobLogGroups() {
        return ResultVO.ok(jobLogService.listJobLogGroups());
    }

}


package com.example.framework.controller;


import com.example.framework.annotation.OptLog;
import com.example.framework.model.dto.ExceptionLogDTO;
import com.example.framework.model.dto.PageResultDTO;
import com.example.framework.model.vo.ConditionVO;
import com.example.framework.service.ExceptionLogService;
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
 * @since 2024-01-11
 */
@Tag(name = "异常日志模块")
@RestController
public class ExceptionLogController {
    @Autowired
    private ExceptionLogService exceptionLogService;

    @Operation(summary = "获取异常日志")
    @GetMapping("/admin/exception/logs")
    public ResultVO<PageResultDTO<ExceptionLogDTO>> listExceptionLogs(ConditionVO conditionVO) {
        return ResultVO.ok(exceptionLogService.listExceptionLogs(conditionVO));
    }

    @OptLog(optType = DELETE)
    @Operation(summary = "删除异常日志")
    @DeleteMapping("/admin/exception/logs")
    public ResultVO<?> deleteExceptionLogs(@RequestBody List<Integer> exceptionLogIds) {
        exceptionLogService.removeByIds(exceptionLogIds);
        return ResultVO.ok();
    }
}


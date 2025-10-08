package com.example.framework.controller;


import com.example.framework.annotation.OptLog;
import com.example.framework.model.dto.OperationLogDTO;
import com.example.framework.model.dto.PageResultDTO;
import com.example.framework.model.vo.ConditionVO;
import com.example.framework.service.OperationLogService;
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
@Tag(name = "操作日志模块")
@RestController
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @Operation(summary = "查看操作日志")
    @GetMapping("/admin/operation/logs")
    public ResultVO<PageResultDTO<OperationLogDTO>> listOperationLogs(ConditionVO conditionVO){
        return ResultVO.ok(operationLogService.listOperationLogs(conditionVO));
    }

    @OptLog(optType = DELETE)
    @Operation(summary = "删除操作日志")
    @DeleteMapping("/admin/operation/logs")
    public ResultVO<?> deleteOperationLogs(@RequestBody List<Integer> operationLogIds) {
        operationLogService.removeByIds(operationLogIds);
        return ResultVO.ok();
    }
}


package com.example.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.framework.entity.OperationLog;
import com.example.framework.model.dto.OperationLogDTO;
import com.example.framework.model.dto.PageResultDTO;
import com.example.framework.model.vo.ConditionVO;

/**
 * @author qinglong
 * @since 2024-01-11
 */
public interface OperationLogService extends IService<OperationLog> {

    PageResultDTO<OperationLogDTO> listOperationLogs(ConditionVO conditionVO);
}

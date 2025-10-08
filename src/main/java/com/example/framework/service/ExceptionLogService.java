package com.example.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.framework.entity.ExceptionLog;
import com.example.framework.model.dto.ExceptionLogDTO;
import com.example.framework.model.dto.PageResultDTO;
import com.example.framework.model.vo.ConditionVO;

/**
 * @author qinglong
 * @since 2024-01-11
 */
public interface ExceptionLogService extends IService<ExceptionLog> {

    PageResultDTO<ExceptionLogDTO> listExceptionLogs(ConditionVO conditionVO);

    void deleteExceptionLogs();
}

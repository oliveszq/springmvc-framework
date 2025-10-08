package com.example.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.framework.entity.ExceptionLog;
import com.example.framework.mapper.ExceptionLogMapper;
import com.example.framework.model.dto.ExceptionLogDTO;
import com.example.framework.model.dto.PageResultDTO;
import com.example.framework.model.vo.ConditionVO;
import com.example.framework.service.ExceptionLogService;
import com.example.framework.utils.BeanCopyUtil;
import com.example.framework.utils.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qinglong
 * @since 2024-01-11
 */
@Service
public class ExceptionLogServiceImpl extends ServiceImpl<ExceptionLogMapper, ExceptionLog> implements ExceptionLogService {

    @Autowired
    private ExceptionLogMapper exceptionLogMapper;

    @Override
    public PageResultDTO<ExceptionLogDTO> listExceptionLogs(ConditionVO conditionVO) {
        Page<ExceptionLog> page = new Page<>(PageUtil.getCurrent(), PageUtil.getSize());

        Page<ExceptionLog> exceptionLogPage = this.page(page, new LambdaQueryWrapper<ExceptionLog>()
                .like(StringUtils.isNotBlank(conditionVO.getKeywords()),
                        ExceptionLog::getOptDesc, conditionVO.getKeywords())
                .orderByDesc(ExceptionLog::getId));

        List<ExceptionLogDTO> exceptionLogDTOs = BeanCopyUtil.copyList(exceptionLogPage.getRecords(),
                ExceptionLogDTO.class);
        return new PageResultDTO<>(exceptionLogDTOs, (int) exceptionLogPage.getTotal());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteExceptionLogs() {
        exceptionLogMapper.delete(new LambdaQueryWrapper<ExceptionLog>()
                .lt(ExceptionLog::getCreateTime, LocalDateTime.now().minusDays(7)));
    }
}

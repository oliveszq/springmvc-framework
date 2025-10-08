package com.example.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.framework.entity.JobLog;
import com.example.framework.model.dto.JobLogDTO;
import com.example.framework.model.dto.PageResultDTO;
import com.example.framework.model.vo.JobLogSearchVO;

import java.util.List;

/**
 *
 * @author qinglong
 * @since 2024-08-27
 */
public interface JobLogService extends IService<JobLog> {

    void cleanJobLogs();

    PageResultDTO<JobLogDTO> listJobLogs(JobLogSearchVO jobLogSearchVO);

    void deleteJobLogs(List<Integer> ids);

    List<String> listJobLogGroups();

}

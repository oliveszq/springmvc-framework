package com.example.framework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.framework.entity.JobLog;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author qinglong
 * @since 2024-08-27
 */
@Repository
public interface JobLogMapper extends BaseMapper<JobLog> {

    List<String> listJobLogGroups();
}

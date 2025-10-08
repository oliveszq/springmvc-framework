package com.example.framework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.framework.entity.Job;
import com.example.framework.model.dto.JobDTO;
import com.example.framework.model.vo.JobSearchVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author qinglong
 * @since 2024-08-27
 */
@Repository
public interface JobMapper extends BaseMapper<Job> {

    Integer countJobs(@Param("jobSearchVO") JobSearchVO jobSearchVO);

    List<JobDTO> listJobs(@Param("current") Long limitCurrent,
                          @Param("size") Long size,
                          @Param("jobSearchVO") JobSearchVO jobSearchVO);

    List<String> listJobGroups();
}

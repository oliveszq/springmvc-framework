package com.example.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.framework.entity.Job;
import com.example.framework.model.dto.JobDTO;
import com.example.framework.model.dto.PageResultDTO;
import com.example.framework.model.vo.JobRunVO;
import com.example.framework.model.vo.JobSearchVO;
import com.example.framework.model.vo.JobStatusVO;
import com.example.framework.model.vo.JobVO;

import java.util.List;

/**
 * @author qinglong
 * @since 2024-08-27
 */
public interface JobService extends IService<Job> {

    PageResultDTO<JobDTO> listJobs(JobSearchVO jobSearchVO);

    List<String> listJobGroups();

    void saveJob(JobVO jobVO);

    void updateJob(JobVO jobVO);

    void deleteJobs(List<Integer> jobIds);

    JobDTO getJobById(Integer jobId);

    void updateJobStatus(JobStatusVO jobStatusVO);

    void runJob(JobRunVO jobRunVO);
}

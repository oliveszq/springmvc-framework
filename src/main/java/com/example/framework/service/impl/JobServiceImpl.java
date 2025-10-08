package com.example.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.framework.entity.Job;
import com.example.framework.enums.JobStatusEnum;
import com.example.framework.mapper.JobMapper;
import com.example.framework.model.dto.JobDTO;
import com.example.framework.model.dto.PageResultDTO;
import com.example.framework.model.vo.JobRunVO;
import com.example.framework.model.vo.JobSearchVO;
import com.example.framework.model.vo.JobStatusVO;
import com.example.framework.model.vo.JobVO;
import com.example.framework.service.JobService;
import com.example.framework.utils.BeanCopyUtil;
import com.example.framework.utils.CronUtil;
import com.example.framework.utils.PageUtil;
import com.example.framework.utils.ScheduleUtil;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author qinglong
 * @since 2024-08-27
 */
@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobMapper jobMapper ;

    @SneakyThrows
    @PostConstruct
    public void init() {
        scheduler.clear();
        List<Job> jobs = jobMapper.selectList(null);
        for (Job job : jobs){
            ScheduleUtil.createScheduleJob(scheduler, job);
        }
    }

    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void saveJob(JobVO jobVO) {
        checkCronIsValid(jobVO);
        Job job = BeanCopyUtil.copyObject(jobVO, Job.class);
        int row = jobMapper.insert(job);
        if (row > 0) ScheduleUtil.createScheduleJob(scheduler, job);
    }

    @SneakyThrows
    @Override
    public PageResultDTO<JobDTO> listJobs(JobSearchVO jobSearchVO) {
        CompletableFuture<Integer> asyncCount =  CompletableFuture.supplyAsync(() ->
                jobMapper.countJobs(jobSearchVO));
        List<JobDTO> jobDTOs = jobMapper.listJobs(PageUtil.getLimitCurrent(), PageUtil.getSize(), jobSearchVO);
        return new PageResultDTO<>(jobDTOs, asyncCount.get());
    }

    @Override
    public List<String> listJobGroups() {
        return jobMapper.listJobGroups();
    }

    private void checkCronIsValid(JobVO jobVO) {
        boolean valid = CronUtil.isValid(jobVO.getCronExpression());
        Assert.isTrue(valid, "Cron表达式无效!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateJob(JobVO jobVO) {
        checkCronIsValid(jobVO);
        Job temp = jobMapper.selectById(jobVO.getId());
        Job job = BeanCopyUtil.copyObject(jobVO, Job.class);
        int row = jobMapper.updateById(job);
        if(row > 0) updateSchedulerJob(job, temp.getJobGroup());
    }

    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobs(List<Integer> jobIds) {
        List<Job> jobs = jobMapper.selectList(new LambdaQueryWrapper<Job>().in(Job::getId, jobIds));
        int row = jobMapper.delete(new LambdaQueryWrapper<Job>().in(Job::getId, jobIds));
        if (row > 0){
            jobs.forEach(item ->{
                try {
                    scheduler.deleteJob(ScheduleUtil.getJobKey(item.getId(), item.getJobGroup()));
                } catch (SchedulerException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Override
    public JobDTO getJobById(Integer jobId) {
        Job job = jobMapper.selectById(jobId);
        JobDTO jobDTO = BeanCopyUtil.copyObject(job, JobDTO.class);
        Date nextExecution = CronUtil.getNextExecution(jobDTO.getCronExpression());
        jobDTO.setNextValidTime(nextExecution);
        return jobDTO;
    }

    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateJobStatus(JobStatusVO jobStatusVO) {
        Job job = jobMapper.selectById(jobStatusVO.getId());
        if (job.getStatus().equals(jobStatusVO.getStatus())) {
            return;
        }
        Integer status = jobStatusVO.getStatus();
        Integer jobId = job.getId();
        String jobGroup = job.getJobGroup();
        LambdaUpdateWrapper<Job> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Job::getId, jobStatusVO.getId()).set(Job::getStatus, status);
        int row = jobMapper.update(null, updateWrapper);
        if (row > 0) {
            if (JobStatusEnum.NORMAL.getValue().equals(status)) {
                scheduler.resumeJob(ScheduleUtil.getJobKey(jobId, jobGroup));
            } else if (JobStatusEnum.PAUSE.getValue().equals(status)) {
                scheduler.pauseJob(ScheduleUtil.getJobKey(jobId, jobGroup));
            }
        }
    }

    @SneakyThrows
    @Override
    public void runJob(JobRunVO jobRunVO) {
        Integer jobId = jobRunVO.getId();
        String jobGroup = jobRunVO.getJobGroup();
        // 获取任务对象
        scheduler.triggerJob(ScheduleUtil.getJobKey(jobId, jobGroup));
    }

    /**
     * 更新调度任务
     *
     * @param job    要更新的任务对象，包含任务的详细信息
     * @param jobGroup 任务组名称，用于区分不同任务组中的任务
     *
     * 此方法首先通过JobKey检查指定的任务是否已存在，如果存在，则删除旧的任务配置
     * 这是因为我们可能需要更改任务的触发器、时间表达式或者其他配置，而更新任务的
     * 前提是先移除原有的任务，然后基于新的Job对象重新创建任务这样能确保任务的
     * 配置是最新的如果任务不存在，则直接创建新的任务，避免了不必要的删除操作
     */
    @SneakyThrows
    public void updateSchedulerJob(Job job, String jobGroup) {
        // 获取任务ID
        Integer jobId = job.getId();
        // 构建任务的唯一标识
        JobKey jobKey = ScheduleUtil.getJobKey(jobId, jobGroup);
        // 检查任务是否存在，如果存在则删除原有任务
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }
        // 重新创建任务，无论是新任务还是更新后的任务
        ScheduleUtil.createScheduleJob(scheduler, job);
    }
}

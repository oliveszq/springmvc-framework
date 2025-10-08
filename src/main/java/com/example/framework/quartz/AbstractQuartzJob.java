package com.example.framework.quartz;


import com.example.framework.constant.ScheduleConstant;
import com.example.framework.entity.Job;
import com.example.framework.entity.JobLog;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @author: qinglong
 * @create-date: 2024/8/29 10:23
 */
abstract class AbstractQuartzJob implements org.quartz.Job {

    private static final Logger log = LoggerFactory.getLogger(AbstractQuartzJob.class);
    private static final ThreadLocal<Date> THREAD_LOCAL = new ThreadLocal<>();


    /**
     * 执行定时任务的方法
     *
     * @param context 任务执行上下文对象，包含了任务执行所需的信息和数据
     * @throws JobExecutionException 当任务执行过程中发生异常时抛出此异常
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Job job = new Job();
        // 获取任务对象
        BeanUtils.copyProperties(context.getMergedJobDataMap().get(ScheduleConstant.TASK_PROPERTIES), job);
        try {
            before(context, job);
            doExecute(context, job);
            after(context, job, null);
        }catch (Exception e){
            log.error("任务执行异常:", e);
            after(context, job, e);
        }
    }
    /**
     * 在任务执行前的操作
     *
     * 此方法在任务执行前被调用，主要用于初始化操作或执行一些前置任务
     * 在这里，将当前时间保存到一个线程本地变量中，这可能用于后续操作中记录任务的开始时间或执行时长
     *
     * @param context JobExecutionContext 是一个传递给调度程序的方法的对象 它包含执行调度程序的方法以及有关触发器和作业的信息
     * @param job Job 类的实例，表示一个任务 这个参数表示正在执行的具体任务实例
     */
    protected void before(JobExecutionContext context, Job job) {
        THREAD_LOCAL.set(new Date());
    }



    /**
     * 任务执行完成后执行此方法，用于记录任务执行完毕后的相关信息
     *
     * @param context 调度上下文，用于获取调度相关的信息
     * @param job 被执行的Job实例，包含任务的详细信息
     * @param e 异常，如果任务执行过程中抛出了异常，则会被传递至此方法
     */
    protected void after(JobExecutionContext context, Job job, Exception e) {
        // 获取线程本地变量中存储的开始时间
        Date startTime = THREAD_LOCAL.get();
        // 清除线程本地变量中的开始时间，避免影响后续任务
        THREAD_LOCAL.remove();
        // 创建一个新的JobLog实例，用于记录任务执行日志
        final JobLog jobLog = new JobLog();
        // 设置任务ID
        jobLog.setJobId(job.getId());
        // 设置任务名称
        jobLog.setJobName(job.getJobName());
        // 设置任务组
        jobLog.setJobGroup(job.getJobGroup());
        // 设置任务的调用目标
        jobLog.setInvokeTarget(job.getInvokeTarget());
        // 设置任务开始时间
        jobLog.setStartTime(startTime);
        // 设置任务结束时间
        jobLog.setEndTime(new Date());
        // 计算任务执行的总耗时（毫秒）
        long runMs = jobLog.getEndTime().getTime() - jobLog.getStartTime().getTime();
        // 设置任务执行信息，包括任务名称和总耗时
        jobLog.setJobMessage(jobLog.getJobName() + " 总共耗时：" + runMs + "毫秒");
    }

    protected abstract void doExecute(JobExecutionContext context, Job job) throws Exception;
}

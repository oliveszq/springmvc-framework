package com.example.framework.utils;

import com.example.framework.constant.ScheduleConstant;
import com.example.framework.entity.Job;
import com.example.framework.enums.JobStatusEnum;
import com.example.framework.exception.TaskException;
import com.example.framework.quartz.QuartzDisallowConcurrentExecution;
import com.example.framework.quartz.QuartzJobExecution;
import org.quartz.*;

public class ScheduleUtil {

    // 获取触发器key
    private static Class<? extends org.quartz.Job> getQuartzJobClass(Job job) {
        boolean isConcurrent = Integer.valueOf(1).equals(job.getConcurrent());
        return isConcurrent ? QuartzJobExecution.class : QuartzDisallowConcurrentExecution.class;
    }

    // 获取触发器的唯一标识
    // 通过任务ID和任务组拼接出触发器的名称和组，用于在调度器中唯一标识一个触发器
    public static TriggerKey getTriggerKey(Integer jobId, String jobGroup) {
        // 拼接触发器名称，格式为"TASK_CLASS_NAME+jobId"
        // 返回触发器的Key，包括名称和组
        return TriggerKey.triggerKey(ScheduleConstant.TASK_CLASS_NAME + jobId, jobGroup);
    }


    //
    public static JobKey getJobKey(Integer jobId, String jobGroup) {
        return JobKey.jobKey(ScheduleConstant.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 创建或更新定时任务
     * 该方法主要用于在调度器中创建或更新一个定时任务，它通过传入的调度器实例和任务对象
     * 来生成对应的Quartz Job和Trigger，并最终在调度器中执行该任务
     *
     * @param scheduler 调度器实例，用于操作定时任务
     * @param job 需要被调度的任务对象，包含任务的详细信息和配置
     * @throws SchedulerException 如果在操作调度器时发生错误
     * @throws TaskException 如果任务本身出现问题
     */
    public static void createScheduleJob(Scheduler scheduler, Job job) throws SchedulerException, TaskException {
        // 获取任务对应的Quartz Job类
        Class<? extends org.quartz.Job> jobClass = getQuartzJobClass(job);
        // 获取任务ID
        Integer jobId = job.getId();
        // 获取任务组名
        String jobGroup = job.getJobGroup();
        // 创建JobDetail实例，指定Job类和任务的标识
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(getJobKey(jobId, jobGroup)).build();
        // 创建Cron触发器，指定Cron表达式
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
        // 处理Cron触发器的误火策略
        cronScheduleBuilder = handleCronScheduleMisfirePolicy(job, cronScheduleBuilder);
        // 创建CronTrigger实例，指定触发器的标识和调度计划
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(jobId, jobGroup))
                .withSchedule(cronScheduleBuilder).build();
        // 将任务属性放入Job的数据映射中，以便Job执行时访问
        jobDetail.getJobDataMap().put(ScheduleConstant.TASK_PROPERTIES, job);
        // 如果任务已存在，则先删除旧的任务
        if (scheduler.checkExists(getJobKey(jobId, jobGroup))) {
            scheduler.deleteJob(getJobKey(jobId, jobGroup));
        }
        // 在调度器中创建或更新任务及其触发器
        scheduler.scheduleJob(jobDetail, trigger);
        // 如果任务状态为暂停，则暂停该任务
        if (job.getStatus().equals(JobStatusEnum.PAUSE.getValue())) {
            scheduler.pauseJob(ScheduleUtil.getJobKey(jobId, jobGroup));
        }
    }

    /**
     * 处理Cron调度任务的误触发策略
     * 根据任务定义中的误触发策略，配置Cron调度构建器的误触发处理指令
     *
     * @param job 调度任务对象，包含任务的详细定义和误触发策略
     * @param cb Cron调度构建器对象，用于配置和构建Cron调度任务
     * @return 配置了误触发处理指令的Cron调度构建器对象
     * @throws TaskException 如果任务的误触发策略不适用于Cron调度任务时抛出任务异常
     */
    public static CronScheduleBuilder handleCronScheduleMisfirePolicy(Job job, CronScheduleBuilder cb)
                throws TaskException {
        // 根据任务定义中的误触发策略，选择相应的处理逻辑
        switch (job.getMisfirePolicy()) {
            case ScheduleConstant.MISFIRE_DEFAULT:
                // 默认策略，不作特殊处理，直接返回原始的Cron调度构建器对象
                return cb;
            case ScheduleConstant.MISFIRE_IGNORE_MISFIRES:
                // 忽略误触发，调整下一个触发时间时忽略误触发的时间点
                return cb.withMisfireHandlingInstructionIgnoreMisfires();
            case ScheduleConstant.MISFIRE_FIRE_AND_PROCEED:
                // 发生误触发时立即执行并继续按原计划执行后续的触发
                return cb.withMisfireHandlingInstructionFireAndProceed();
            case ScheduleConstant.MISFIRE_DO_NOTHING:
                // 发生误触发时不执行任何操作，仅记录误触发事件
                return cb.withMisfireHandlingInstructionDoNothing();
            default:
                // 不支持的误触发策略，抛出配置错误的异常
                throw new TaskException("The task misfire policy '" + job.getMisfirePolicy()
                        + "' cannot be used in cron schedule tasks", TaskException.Code.CONFIG_ERROR);
        }
    }
}

package com.example.framework.quartz;


import com.example.framework.entity.Job;
import com.example.framework.utils.JobInvokeUtil;
import org.quartz.JobExecutionContext;

// 不允许并发执行
public class QuartzDisallowConcurrentExecution extends AbstractQuartzJob {
    // 执行任务
    @Override
    protected void doExecute(JobExecutionContext context, Job job) throws Exception {

        JobInvokeUtil.invokeMethod(job);
    }
}

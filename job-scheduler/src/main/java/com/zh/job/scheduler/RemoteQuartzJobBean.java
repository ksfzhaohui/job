package com.zh.job.scheduler;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.zh.job.common.IJobHandler;
import com.zh.job.common.bean.Result;

/**
 * 远程QuartzJobBean，调度端没有具体的QuartzJobBean
 * 
 * @author hui.zhao.cfs
 *
 */
public class RemoteQuartzJobBean extends QuartzJobBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteQuartzJobBean.class);

	private ExecutorBean executorBean;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		JobKey jobKey = context.getTrigger().getJobKey();
		LOGGER.info("jobName:" + jobKey.getName() + ",group:" + jobKey.getGroup());
		IJobHandler executor = JobProxy.create(IJobHandler.class, jobKey, this.executorBean);
		Result result;
		try {
			result = executor.execute("");
			LOGGER.info("result:" + result);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	public ExecutorBean getExecutorBean() {
		return executorBean;
	}

	public void setExecutorBean(ExecutorBean executorBean) {
		this.executorBean = executorBean;
	}

}
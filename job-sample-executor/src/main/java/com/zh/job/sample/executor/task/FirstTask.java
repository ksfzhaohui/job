package com.zh.job.sample.executor.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zh.job.common.IJobHandler;
import com.zh.job.common.bean.Result;
import com.zh.job.executor.ExecutorTask;

@ExecutorTask(name = "firstTask")
public class FirstTask implements IJobHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(FirstTask.class);

	@Override
	public Result execute(String param) throws Exception {
		LOGGER.info("execute firstTask");
		return SUCCESS;
	}

}

package com.zh.job.sample.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ExecutorBootstrap {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorBootstrap.class);

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		LOGGER.debug("start server");
		new ClassPathXmlApplicationContext("spring.xml");
	}
}

package com.zh.job.sample.scheduler;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SchedulerBootstrap {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("quartz.xml");
	}
}

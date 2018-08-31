package com.zh.job.scheduler;

import java.io.Serializable;

/**
 * 执行器Bean
 * 
 * @author hui.zhao.cfs
 *
 */
public class ExecutorBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String executorName;
	private String discoveryAddress;

	public ExecutorBean(String executorName, String discoveryAddress) {
		this.executorName = executorName;
		this.discoveryAddress = discoveryAddress;
	}

	public String getExecutorName() {
		return executorName;
	}

	public void setExecutorName(String executorName) {
		this.executorName = executorName;
	}

	public String getDiscoveryAddress() {
		return discoveryAddress;
	}

	public void setDiscoveryAddress(String discoveryAddress) {
		this.discoveryAddress = discoveryAddress;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}

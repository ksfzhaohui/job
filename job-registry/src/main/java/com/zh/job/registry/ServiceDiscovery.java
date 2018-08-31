package com.zh.job.registry;

public interface ServiceDiscovery {

	/**
	 * 发现服务
	 * 
	 * @param serviceName
	 *            服务器名称
	 * @return 服务地址
	 */
	public String discover(String serviceName) throws Exception;
}

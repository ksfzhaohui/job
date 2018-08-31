package com.zh.job.scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.zh.job.registry.ServiceDiscovery;
import com.zh.job.registry.impl.ZookeeperServiceDiscovery;

/**
 * 服务发现工厂类
 * 
 * @author hui.zhao.cfs
 *
 */
public class ServiceDiscoveryFactory {

	private static Map<String, ServiceDiscovery> discoveryMap = new ConcurrentHashMap<>();

	public static ServiceDiscovery getServiceDiscovery(String zkAddress) {
		ServiceDiscovery serviceDiscovery = discoveryMap.get(zkAddress);
		if (serviceDiscovery != null) {
			return serviceDiscovery;
		}

		synchronized (ServiceDiscoveryFactory.class) {
			serviceDiscovery = discoveryMap.get(zkAddress);
			if (serviceDiscovery == null) {
				serviceDiscovery = new ZookeeperServiceDiscovery(zkAddress);
				discoveryMap.put(zkAddress, serviceDiscovery);
			}

			return serviceDiscovery;
		}
	}

}

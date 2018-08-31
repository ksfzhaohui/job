package com.zh.job.registry.impl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zh.job.registry.Constant;
import com.zh.job.registry.ServiceRegistry;

/**
 * zk服务注册
 * 
 * @author hui.zhao.cfs
 *
 */
public class ZookeeperServiceRegistry implements ServiceRegistry {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceRegistry.class);

	private CuratorFramework zkClient;

	public ZookeeperServiceRegistry(String zkAddress) {
		zkClient = CuratorFrameworkFactory.builder().connectString(zkAddress)
				.sessionTimeoutMs(Constant.ZK_SESSION_TIMEOUT).connectionTimeoutMs(Constant.ZK_CONNECTION_TIMEOUT)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		zkClient.start();
		LOGGER.debug("connect zookeeper");
	}

	@Override
	public void register(String serviceName, String serviceAddress) throws Exception {
		// 创建 registry 节点（持久）
		String registryPath = Constant.ZK_REGISTRY_PATH;
		// 创建 service 节点（持久）
		String servicePath = registryPath + "/" + serviceName;
		Stat stat = zkClient.checkExists().forPath(servicePath);
		if (stat == null) {
			zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(servicePath);
		}

		// 创建 address 节点（临时）
		String addressPath = servicePath + "/address-";
		String addressNode = zkClient.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(addressPath,
				serviceAddress.getBytes());
		LOGGER.debug("create address node: {}", addressNode);
	}
}

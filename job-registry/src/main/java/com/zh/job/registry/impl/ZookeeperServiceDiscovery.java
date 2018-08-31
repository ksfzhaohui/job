package com.zh.job.registry.impl;

import java.util.List;
import java.util.Random;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zh.job.registry.Constant;
import com.zh.job.registry.ServiceDiscovery;

/**
 * zk服务发现
 * 
 * @author hui.zhao.cfs
 *
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class);

	private CuratorFramework zkClient;

	public ZookeeperServiceDiscovery(String zkAddress) {
		zkClient = CuratorFrameworkFactory.builder().connectString(zkAddress)
				.sessionTimeoutMs(Constant.ZK_SESSION_TIMEOUT).connectionTimeoutMs(Constant.ZK_CONNECTION_TIMEOUT)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		zkClient.start();
		LOGGER.debug("connect zookeeper");
	}

	@Override
	public String discover(String serviceName) throws Exception {
		// 获取 service 节点
		String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
		Stat stat = zkClient.checkExists().forPath(servicePath);
		if (stat == null) {
			throw new RuntimeException(String.format("can not find any service node on path: %s", servicePath));
		}
		List<String> addressList = zkClient.getChildren().forPath(servicePath);
		if (addressList == null || addressList.size() == 0) {
			throw new RuntimeException(String.format("can not find any address node on path: %s", servicePath));
		}
		// 获取 address 节点
		String address;
		int size = addressList.size();
		if (size == 1) {
			// 若只有一个地址，则获取该地址
			address = addressList.get(0);
			LOGGER.debug("get only address node: {}", address);
		} else {
			// 若存在多个地址，则随机获取一个地址
			address = addressList.get(new Random().nextInt(size));
			LOGGER.debug("get random address node: {}", address);
		}
		// 获取 address 节点的值
		String addressPath = servicePath + "/" + address;
		return new String(zkClient.getData().forPath(addressPath));
	}

}

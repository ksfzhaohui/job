package com.zh.job.scheduler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zh.job.common.bean.Request;
import com.zh.job.common.bean.Response;
import com.zh.job.common.util.StringUtil;
import com.zh.job.registry.ServiceDiscovery;

public class JobProxy {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobProxy.class);

	@SuppressWarnings("unchecked")
	public static <T> T create(final Class<?> interfaceClass, final JobKey jobKey, final ExecutorBean executor) {
		// 创建动态代理对象
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						// 创建 RPC 请求对象并设置请求属性
						Request request = new Request();
						request.setRequestId(UUID.randomUUID().toString());
						request.setInterfaceName(jobKey.getName());
						request.setMethodName(method.getName());
						request.setParameterTypes(method.getParameterTypes());
						request.setParameters(args);

						String serviceAddress = null;
						ServiceDiscovery serviceDiscovery = ServiceDiscoveryFactory
								.getServiceDiscovery(executor.getDiscoveryAddress());
						// 获取 RPC 服务地址
						if (serviceDiscovery != null) {
							serviceAddress = serviceDiscovery.discover(executor.getExecutorName());
							LOGGER.debug("discover service: {} => {}", executor.getExecutorName(), serviceAddress);
						}
						if (StringUtil.isEmpty(serviceAddress)) {
							throw new RuntimeException("server address is empty");
						}
						// 从 RPC 服务地址中解析主机名与端口号
						String[] array = StringUtil.split(serviceAddress, ":");
						String host = array[0];
						int port = Integer.parseInt(array[1]);
						// 创建 RPC 客户端对象并发送 RPC 请求
						ExecutorClient client = new ExecutorClient(host, port);
						long time = System.currentTimeMillis();
						Response response = client.send(request);
						LOGGER.debug("time: {}ms", System.currentTimeMillis() - time);
						if (response == null) {
							throw new RuntimeException("response is null");
						}
						// 返回 RPC 响应结果
						if (response.hasException()) {
							throw response.getException();
						} else {
							return response.getResult();
						}
					}
				});
	}

}

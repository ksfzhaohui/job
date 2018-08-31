package com.zh.job.executor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.zh.job.common.bean.Request;
import com.zh.job.common.bean.Response;
import com.zh.job.common.codec.RpcDecoder;
import com.zh.job.common.codec.RpcEncoder;
import com.zh.job.registry.ServiceRegistry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 执行器服务
 * 
 * @author hui.zhao.cfs
 *
 */
public class ExecutorServer implements ApplicationContextAware, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorServer.class);

	/** 执行器名称 **/
	private String executorName;

	/** 执行器地址 **/
	private String serviceAddress;

	/** 注册服务地址 **/
	private ServiceRegistry serviceRegistry;

	/**
	 * 存放 服务名 与 服务对象 之间的映射关系
	 */
	private Map<String, Object> handlerMap = new HashMap<String, Object>();

	public ExecutorServer(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}

	public ExecutorServer(String executorName, String serviceAddress, ServiceRegistry serviceRegistry) {
		this.executorName = executorName;
		this.serviceAddress = serviceAddress;
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		// 扫描带有 ExecutorTask 注解的类并初始化 handlerMap 对象
		Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(ExecutorTask.class);
		if (MapUtils.isNotEmpty(serviceBeanMap)) {
			for (Object serviceBean : serviceBeanMap.values()) {
				ExecutorTask rpcService = serviceBean.getClass().getAnnotation(ExecutorTask.class);
				String serviceName = rpcService.name();
				handlerMap.put(serviceName, serviceBean);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			// 创建并初始化 Netty 服务端 Bootstrap 对象
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup);
			bootstrap.channel(NioServerSocketChannel.class);
			bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel channel) throws Exception {
					ChannelPipeline pipeline = channel.pipeline();
					pipeline.addLast(new RpcDecoder(Request.class));
					pipeline.addLast(new RpcEncoder(Response.class));
					pipeline.addLast(new ExecutorServerHandler(handlerMap));
				}
			});
			bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			// 获取 RPC 服务器的 IP 地址与端口号
			String[] addressArray = StringUtils.splitByWholeSeparator(serviceAddress, ":");
			String ip = addressArray[0];
			int port = Integer.parseInt(addressArray[1]);
			// 启动 RPC 服务器
			ChannelFuture future = bootstrap.bind(ip, port).sync();
			// 注册 RPC 服务地址
			if (serviceRegistry != null) {
				serviceRegistry.register(executorName, serviceAddress);
				LOGGER.info("register service: {} => {}", executorName, serviceAddress);
			}
			LOGGER.info("server started on port {}", port);
			// 关闭 RPC 服务器
			future.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	public String getExecutorName() {
		return executorName;
	}

	public void setExecutorName(String executorName) {
		this.executorName = executorName;
	}

}

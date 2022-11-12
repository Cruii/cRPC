package io.cruii.crpc.provider;

import io.cruii.crpc.codec.RpcDecoder;
import io.cruii.crpc.codec.RpcEncoder;
import io.cruii.crpc.common.RpcServiceHelper;
import io.cruii.crpc.common.ServiceMeta;
import io.cruii.crpc.common.annotation.RpcService;
import io.cruii.crpc.handler.RpcRequestHandler;
import io.cruii.crpc.registry.RegistryService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cruii
 * Created on 2022/11/11
 */
public@Slf4j
class RpcProvider implements InitializingBean, BeanPostProcessor {
    private String serverAddress;

    private final int serverPort;

    private final RegistryService serviceRegistry;

    private final Map<String, Object> serviceMap = new HashMap<>();

    public RpcProvider(int serverPort, RegistryService serviceRegistry) {
        this.serverPort = serverPort;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void afterPropertiesSet() {
        new Thread(() -> {
            try {
                startRpcServer();
            } catch (Exception e) {
                log.error("启动RPC服务器失败", e);
            }
        }).start();
    }

    private void startRpcServer() throws Exception {
        serverAddress = InetAddress.getLocalHost().getHostAddress();

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new RpcEncoder())
                                .addLast(new RpcDecoder())
                                .addLast(new RpcRequestHandler(serviceMap));
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(serverAddress, serverPort).sync();
            log.info("server addr {} started on port {}", serverAddress, serverPort);
            channelFuture.channel().closeFuture().sync();
        } finally {
            log.info("关闭bossGroup和workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 扫描@RpcService注解
        RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
        if (rpcService != null) {
            // 获取注解元数据信息
            String serviceName = rpcService.serviceInterface().getName();
            String version = rpcService.version();

            // 生成服务元数据信息
            ServiceMeta serviceMeta = new ServiceMeta();
            serviceMeta.setName(serviceName);
            serviceMeta.setVersion(version);
            serviceMeta.setAddress(serverAddress);
            serviceMeta.setPort(serverPort);

            try {
                // 向注册中心注册服务
                serviceRegistry.register(serviceMeta);
            } catch (Exception e) {
                log.error("failed to register service {}#{}", serviceName, version, e);
            }

            // 缓存服务对应的bean
            serviceMap.put(RpcServiceHelper.buildServiceKey(serviceName, version), bean);
        }

        return bean;
    }
}

package io.cruii.crpc.provider.config;

import io.cruii.crpc.codec.RpcDecoder;
import io.cruii.crpc.codec.RpcEncoder;
import io.cruii.crpc.common.RpcServiceHelper;
import io.cruii.crpc.common.ServiceMeta;
import io.cruii.crpc.common.annotation.RpcService;
import io.cruii.crpc.handler.RpcRequestHandler;
import io.cruii.crpc.provider.RpcProvider;
import io.cruii.crpc.registry.RegistryService;
import io.cruii.crpc.registry.RegistryType;
import io.cruii.crpc.registry.factory.RegistryFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cruii
 * Created on 2022/11/8
 */
@ConfigurationProperties("rpc.provider")
@Configuration
@Data
public class RpcProviderConfiguration {

    private int port;

    private String registerType;

    private String registerAddr;

    @Bean
    public RpcProvider init() throws Exception {
        RegistryType registryType = RegistryType.valueOf(registerType);
        RegistryService registryService = RegistryFactory.getInstance(registerAddr, registryType);
        return new RpcProvider(port, registryService);
    }
}

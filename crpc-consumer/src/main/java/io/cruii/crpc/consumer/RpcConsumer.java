package io.cruii.crpc.consumer;

import io.cruii.crpc.codec.RpcDecoder;
import io.cruii.crpc.codec.RpcEncoder;
import io.cruii.crpc.common.RpcRequest;
import io.cruii.crpc.common.RpcServiceHelper;
import io.cruii.crpc.common.ServiceMeta;
import io.cruii.crpc.handler.RpcResponseHandler;
import io.cruii.crpc.protocol.RpcProtocol;
import io.cruii.crpc.registry.RegistryService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author cruii
 * Created on 2022/11/11
 */
@Slf4j
public class RpcConsumer {
    private final Bootstrap bootstrap;

    private final EventLoopGroup eventLoopGroup;

    public RpcConsumer() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new RpcEncoder())
                                .addLast(new RpcDecoder())
                                .addLast(new RpcResponseHandler());
                    }
                });
    }

    public void doRequest(RpcProtocol<RpcRequest> protocol, RegistryService registryService) throws Exception {
        RpcRequest rpcRequest = protocol.getBody();
        Object[] params = rpcRequest.getParams();

        // 生成调用服务对应的Key
        String serviceKey = RpcServiceHelper.buildServiceKey(rpcRequest.getClassName(), rpcRequest.getServiceVersion());
        int hashCode = params.length > 0 ? params[0].hashCode() : serviceKey.hashCode();
        ServiceMeta serviceMeta = registryService.discovery(serviceKey, hashCode);
        if (serviceMeta != null) {
            String serviceAddress = serviceMeta.getAddress();
            int port = serviceMeta.getPort();
            ChannelFuture channelFuture = bootstrap.connect(serviceAddress, port).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        log.info("Connect to server {}:{} success.", serviceAddress, port);
                    } else {
                        log.error("Connect to server {}:{} failed.", serviceAddress, port, channelFuture.cause());
                        eventLoopGroup.shutdownGracefully();
                    }
                }
            });

            channelFuture.channel().writeAndFlush(protocol);
        }
    }
}

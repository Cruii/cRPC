package io.cruii.crpc.handler;

import io.cruii.crpc.common.RpcFuture;
import io.cruii.crpc.common.RpcRequestHolder;
import io.cruii.crpc.common.RpcResponse;
import io.cruii.crpc.protocol.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author cruii
 * Created on 2022/11/11
 */
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                RpcProtocol<RpcResponse> protocol) throws Exception {
        long requestId = protocol.getHeader().getRequestId();
        RpcFuture<RpcResponse> future = RpcRequestHolder.REQUEST_MAP.remove(requestId);
        future.getPromise().setSuccess(protocol.getBody());
    }
}

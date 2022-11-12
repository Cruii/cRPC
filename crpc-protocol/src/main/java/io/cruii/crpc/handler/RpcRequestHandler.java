package io.cruii.crpc.handler;

import io.cruii.crpc.common.RpcRequest;
import io.cruii.crpc.common.RpcResponse;
import io.cruii.crpc.common.RpcServiceHelper;
import io.cruii.crpc.protocol.MessageHeader;
import io.cruii.crpc.protocol.MessageStatus;
import io.cruii.crpc.protocol.MessageType;
import io.cruii.crpc.protocol.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author cruii
 * Created on 2022/11/11
 */
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {
    private final Map<String, Object> rpcServiceMap;

    public RpcRequestHandler(Map<String, Object> rpcServiceMap) {
        this.rpcServiceMap = rpcServiceMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                RpcProtocol<RpcRequest> protocol) throws Exception {
        RpcRequestProcessor.submitRequest(() -> {
            RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
            RpcResponse rpcResponse = new RpcResponse();
            MessageHeader header = protocol.getHeader();
            header.setMessageType(((byte) MessageType.RESPONSE.getType()));
            try {
                Object result = handle(protocol.getBody());
                rpcResponse.setData(result);
                header.setStatus((byte) MessageStatus.SUCCESS.getCode());
                responseRpcProtocol.setHeader(header);
                responseRpcProtocol.setBody(rpcResponse);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }

            channelHandlerContext.writeAndFlush(responseRpcProtocol);
        });
    }

    private Object handle(RpcRequest request) throws InvocationTargetException {
        String className = request.getClassName();
        String serviceVersion = request.getServiceVersion();
        String serviceKey = RpcServiceHelper.buildServiceKey(className, serviceVersion);
        Object serviceBean = rpcServiceMap.get(serviceKey);
        if (serviceBean == null) {
            throw new RuntimeException(String.format("Service not exist: %s:%s", request.getClassName(), request.getMethodName()));
        }
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParams();

        FastClass fastClass = FastClass.create(serviceClass);
        int methodIndex = fastClass.getIndex(methodName, parameterTypes);
        return fastClass.invoke(methodIndex, serviceBean, parameters);
    }
}

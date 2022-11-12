package io.cruii.crpc.consumer;

import io.cruii.crpc.common.RpcFuture;
import io.cruii.crpc.common.RpcRequest;
import io.cruii.crpc.common.RpcRequestHolder;
import io.cruii.crpc.common.RpcResponse;
import io.cruii.crpc.protocol.*;
import io.cruii.crpc.registry.RegistryService;
import io.cruii.crpc.serialization.SerializationTypeEnum;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author cruii
 * Created on 2022/11/11
 */
public class RpcProxy implements InvocationHandler {
    private final String serviceVersion;
    private final long timeout;
    private final RegistryService registryService;

    public RpcProxy(String serviceVersion, long timeout, RegistryService registryService) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.registryService = registryService;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        MessageHeader header = new MessageHeader();
        header.setMagicNum(ProtocolConstant.MAGIC_NUM);
        header.setVersion(ProtocolConstant.version);
        header.setSerialization(((byte) SerializationTypeEnum.HESSIAN.getType()));
        header.setMessageType(((byte) MessageType.REQUEST.getType()));
        header.setStatus((byte) 0x1);
        long requestId = RpcRequestHolder.REQUEST_ID_GENERATOR.get();
        header.setRequestId(requestId);

        RpcRequest request = new RpcRequest();
        request.setServiceVersion(serviceVersion);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParams(args);

        protocol.setHeader(header);
        protocol.setBody(request);


        RpcConsumer rpcConsumer = new RpcConsumer();
        RpcFuture<RpcResponse> rpcFuture = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), timeout);
        RpcRequestHolder.REQUEST_MAP.put(requestId, rpcFuture);
        rpcConsumer.doRequest(protocol, registryService);

        return rpcFuture.getPromise().get(rpcFuture.getTimeout(), TimeUnit.MILLISECONDS).getData();
    }
}

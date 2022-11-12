package io.cruii.crpc.codec;

import io.cruii.crpc.common.RpcRequest;
import io.cruii.crpc.common.RpcResponse;
import io.cruii.crpc.protocol.MessageHeader;
import io.cruii.crpc.protocol.MessageType;
import io.cruii.crpc.protocol.ProtocolConstant;
import io.cruii.crpc.protocol.RpcProtocol;
import io.cruii.crpc.serialization.RpcSerialization;
import io.cruii.crpc.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author cruii
 * Created on 2022/11/11
 */
public class RpcDecoder extends ByteToMessageDecoder {
    /*
    +---------------------------------------------------------------+
    | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte  |
    +---------------------------------------------------------------+
    | 状态 1byte |        消息 ID 8byte     |      数据长度 4byte     |
    +---------------------------------------------------------------+
    |                   数据内容 （长度不定）                          |
    +---------------------------------------------------------------+
    */

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < ProtocolConstant.HEAD_LENGTH) {
            return;
        }
        // 标记读指针
        byteBuf.markReaderIndex();

        // 解析协议
        short magicNum = byteBuf.readShort();
        byte version = byteBuf.readByte();
        byte serialization = byteBuf.readByte();
        byte messageType = byteBuf.readByte();
        byte status = byteBuf.readByte();
        long requestId = byteBuf.readLong();
        int contentLength = byteBuf.readInt();

        if (byteBuf.readableBytes() < contentLength) {
            // 当剩余可读取字节小于数据长度时，意味着数据内容还未传输完毕，重置读指针
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] data = new byte[contentLength];
        byteBuf.readBytes(data);

        MessageType type = MessageType.of(messageType);
        if (type == null) {
            return;
        }

        MessageHeader header = new MessageHeader();
        header.setMagicNum(magicNum);
        header.setVersion(version);
        header.setSerialization(serialization);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setMessageType(messageType);
        header.setContentLength(contentLength);

        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(serialization);
        switch (type) {
            case REQUEST:
                RpcRequest rpcRequest = rpcSerialization.deserialize(data, RpcRequest.class);
                if (rpcRequest != null) {
                    RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(rpcRequest);
                    list.add(protocol);
                }
                break;
            case RESPONSE:
                RpcResponse rpcResponse = rpcSerialization.deserialize(data, RpcResponse.class);
                if (rpcResponse != null) {
                    RpcProtocol<RpcResponse> protocol = new RpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(rpcResponse);
                    list.add(protocol);
                }
                break;
            case HEARTBEAT:
                // todo
                break;
        }
    }
}

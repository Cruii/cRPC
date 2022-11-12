package io.cruii.crpc.codec;

import io.cruii.crpc.common.RpcRequest;
import io.cruii.crpc.protocol.MessageHeader;
import io.cruii.crpc.protocol.RpcProtocol;
import io.cruii.crpc.serialization.RpcSerialization;
import io.cruii.crpc.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author cruii
 * Created on 2022/11/11
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> {
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
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          RpcProtocol<Object> protocol,
                          ByteBuf byteBuf) throws Exception {
        MessageHeader header = protocol.getHeader();
        byteBuf.writeShort(header.getMagicNum());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getSerialization());
        byteBuf.writeByte(header.getMessageType());
        byteBuf.writeByte(header.getStatus());
        byteBuf.writeLong(header.getRequestId());

        RpcSerialization serialization = SerializationFactory.getRpcSerialization(header.getSerialization());
        byte[] body = serialization.serialize(protocol.getBody());
        byteBuf.writeInt(body.length);
        byteBuf.writeBytes(body);
    }

}

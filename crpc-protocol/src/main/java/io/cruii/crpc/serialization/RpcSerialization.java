package io.cruii.crpc.serialization;

import java.io.IOException;

/**
 * @author cruii
 * Created on 2022/11/11
 */
public interface RpcSerialization {
    /**
     * 序列化
     */
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * 反序列化
     */
    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;
}

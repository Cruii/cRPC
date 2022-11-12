package io.cruii.crpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @author cruii
 * Created on 2022/11/11
 */
@Data
public class RpcProtocol<T> implements Serializable {
    private static final long serialVersionUID = 7728307213941487147L;

    private MessageHeader header;

    private T body;
}

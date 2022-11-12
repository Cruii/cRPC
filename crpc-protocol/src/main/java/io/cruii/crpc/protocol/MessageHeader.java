package io.cruii.crpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @author cruii
 * Created on 2022/11/11
 */
@Data
public class MessageHeader implements Serializable {
    private static final long serialVersionUID = 8438761115187646176L;

    private short magicNum;

    private byte version;

    private byte serialization;

    private byte messageType;

    private byte status;

    private long requestId;

    private int contentLength;
}

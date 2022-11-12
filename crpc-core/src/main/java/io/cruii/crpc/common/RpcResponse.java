package io.cruii.crpc.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author cruii
 * Created on 2022/11/11
 */
@Data
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = -4783008428795032456L;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 请求结果
     */
    private Object data;
}

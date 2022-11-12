package io.cruii.crpc.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author cruii
 * Created on 2022/11/11
 */
@Data
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 6201740299063907394L;

    /**
     * 服务方法名
     */
    private String methodName;

    /**
     * 服务版本
     */
    private String serviceVersion;

    /**
     * 服务接口名
     */
    private String className;

    /**
     * 方法参数
     */
    private Object[] params;

    /**
     * 参数类型列表
     */
    private Class<?>[] parameterTypes;
}

package io.cruii.crpc.common;

import io.netty.util.concurrent.Promise;
import lombok.Data;

/**
 * @author cruii
 * Created on 2022/11/11
 */
@Data
public class RpcFuture<T> {
    private Promise<T> promise;

    private long timeout;

    public RpcFuture(Promise<T> promise, long timeout) {
        this.promise = promise;
        this.timeout = timeout;
    }
}

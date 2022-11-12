package io.cruii.crpc.registry.loadbalancer;

import java.util.List;

/**
 * @author cruii
 * Created on 2022/11/9
 */
public interface ServiceLoadBalancer<T> {
    T select(List<T> servers, int hashCode);
}

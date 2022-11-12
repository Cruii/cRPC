package io.cruii.crpc.registry;

import io.cruii.crpc.common.ServiceMeta;

import java.io.IOException;

/**
 * @author cruii
 * Created on 2022/11/9
 */
public interface RegistryService {
    void register(ServiceMeta serviceMeta) throws Exception;

    void unRegister(ServiceMeta serviceMeta) throws Exception;

    ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception;

    void destroy() throws IOException;
}

package io.cruii.crpc.registry;

import io.cruii.crpc.common.ServiceMeta;

import java.io.IOException;

/**
 * @author cruii
 * Created on 2022/11/9
 */
public class NacosRegistryService implements RegistryService {
    public NacosRegistryService(String registryAddr) {

    }

    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {

    }

    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {

    }

    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception {
        return null;
    }

    @Override
    public void destroy() throws IOException {

    }
}

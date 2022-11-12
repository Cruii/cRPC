package io.cruii.crpc.registry.factory;

import io.cruii.crpc.registry.*;

/**
 * 服务注册中心工厂类
 *
 * @author cruii
 * Created on 2022/11/9
 */
public class RegistryFactory {
    private RegistryFactory() {
    }

    private static volatile RegistryService registryService;

    public static RegistryService getInstance(String registryAddr, RegistryType registryType) throws Exception {
        if (registryService == null) {
            synchronized (RegistryFactory.class) {
                if (registryService == null) {
                    switch (registryType) {
                        case ZOOKEEPER:
                            registryService = new ZookeeperRegistryService(registryAddr);
                            break;
                        case EUREKA:
                            registryService = new EurekaRegistryService(registryAddr);
                            break;
                        case NACOS:
                            registryService = new NacosRegistryService(registryAddr);
                            break;
                    }
                }
            }
        }

        return registryService;
    }
}

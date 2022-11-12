package io.cruii.crpc.registry.zookeeper;

import io.cruii.crpc.common.RpcServiceHelper;
import io.cruii.crpc.common.ServiceMeta;
import io.cruii.crpc.registry.RegistryService;
import io.cruii.crpc.registry.loadbalancer.ZKConsistentHashLoadBalancer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @author cruii
 * Created on 2022/11/9
 */
public class ZookeeperRegistryService implements RegistryService {
    public static final int BASE_SLEEP_TIME_MS = 1000;

    public static final int MAX_RETRIES = 3;

    public static final String ZK_BASE_PATH = "/crpc";

    private final ServiceDiscovery<ServiceMeta> serviceDiscovery;

    public ZookeeperRegistryService(String registryAddr) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient(registryAddr, new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .client(client)
                .basePath(ZK_BASE_PATH)
                .serializer(new JsonInstanceSerializer<>(ServiceMeta.class))
                .build();
        serviceDiscovery.start();
    }

    /**
     * 服务注册
     *
     * @param serviceMeta 服务元数据
     */
    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance
                .<ServiceMeta>builder()
                .name(RpcServiceHelper.buildServiceKey(serviceMeta.getName(), serviceMeta.getVersion()))
                .address(serviceMeta.getAddress())
                .port(serviceMeta.getPort())
                .payload(serviceMeta).build();
        serviceDiscovery.registerService(serviceInstance);
    }

    /**
     * 服务下线
     *
     * @param serviceMeta 服务元数据
     */
    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance
                .<ServiceMeta>builder()
                .name(RpcServiceHelper.buildServiceKey(serviceMeta.getName(), serviceMeta.getVersion()))
                .address(serviceMeta.getAddress())
                .port(serviceMeta.getPort())
                .payload(serviceMeta).build();
        serviceDiscovery.unregisterService(serviceInstance);
    }

    /**
     * 服务发现
     *
     * @param serviceName     服务名
     * @param invokerHashCode 服务名的哈希码
     * @return 服务元数据
     */
    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception {
        Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        ZKConsistentHashLoadBalancer loadBalancer = new ZKConsistentHashLoadBalancer();
        return loadBalancer.select(((List<ServiceInstance<ServiceMeta>>) serviceInstances), invokerHashCode).getPayload();
    }

    /**
     * 销毁服务注册中心
     */
    @Override
    public void destroy() throws IOException {
        serviceDiscovery.close();
    }
}

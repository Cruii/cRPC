package io.cruii.crpc.registry.loadbalancer;

import io.cruii.crpc.common.ServiceMeta;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

/**
 * @author cruii
 * Created on 2022/11/9
 */
public class ZKConsistentHashLoadBalancer implements ServiceLoadBalancer<ServiceInstance<ServiceMeta>> {

    /**
     * 哈希环虚拟节点Key的分隔符
     */
    private static final String VIRTUAL_NODE_SPLIT = "#";

    /**
     * 哈希环虚拟节点的个数
     */
    private static final int VIRTUAL_NODE_SIZE = 10;

    /**
     * 负载均衡选择节点
     *
     * @param servers  服务集群
     * @param hashCode 服务哈希码
     * @return 服务实例
     */
    @Override
    public ServiceInstance<ServiceMeta> select(List<ServiceInstance<ServiceMeta>> servers, int hashCode) {
        TreeMap<Integer, ServiceInstance<ServiceMeta>> ring = makeConsistentHashRing(servers);
        return allocateNode(ring, hashCode);
    }

    /**
     * 分配哈希环上的服务节点
     *
     * @param ring     哈希环
     * @param hashCode 哈希码
     * @return 服务实例
     */
    private ServiceInstance<ServiceMeta> allocateNode(TreeMap<Integer, ServiceInstance<ServiceMeta>> ring, int hashCode) {
        return Optional.ofNullable(ring.ceilingEntry(hashCode).getValue())
                .orElseGet(() -> ring.firstEntry().getValue());
    }

    /**
     * 生成一致性哈希环
     *
     * @param servers 服务集群
     * @return 哈希环
     */
    private TreeMap<Integer, ServiceInstance<ServiceMeta>> makeConsistentHashRing(List<ServiceInstance<ServiceMeta>> servers) {
        TreeMap<Integer, ServiceInstance<ServiceMeta>> ring = new TreeMap<>();
        for (ServiceInstance<ServiceMeta> instance : servers) {
            for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
                ring.put((buildServiceInstanceKey(instance) + VIRTUAL_NODE_SPLIT + i).hashCode(), instance);
            }
        }
        return ring;
    }

    /**
     * 生成哈希环上用的服务实例Key
     *
     * @param instance 服务实例
     * @return 服务实例Key
     */
    private String buildServiceInstanceKey(ServiceInstance<ServiceMeta> instance) {
        ServiceMeta payload = instance.getPayload();
        return String.join(":", payload.getAddress(), String.valueOf(payload.getPort()));
    }
}

package io.cruii.crpc.consumer;

import io.cruii.crpc.common.annotation.RpcReference;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author cruii
 * Created on 2022/11/9
 */
public class RpcReferenceBean implements FactoryBean<Object> {
    private Class<?> interfaceClass;

    @Override
    public Object getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }
}

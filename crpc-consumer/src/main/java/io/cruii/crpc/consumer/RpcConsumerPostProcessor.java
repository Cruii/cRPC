package io.cruii.crpc.consumer;

import io.cruii.crpc.common.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author cruii
 * Created on 2022/11/10
 */
@Slf4j
@Component
public class RpcConsumerPostProcessor implements ApplicationContextAware, BeanClassLoaderAware, BeanFactoryPostProcessor {
    private ClassLoader classLoader;

    private ApplicationContext applicationContext;

    private final Map<String, BeanDefinition> rpcReferenceBeanDefinitions = new LinkedHashMap<>();

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null) {
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, classLoader);
                ReflectionUtils.doWithFields(clazz, this::parseRpcReference);
            }
        }

        // 注册BeanDefinition
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        rpcReferenceBeanDefinitions.forEach((beanName, beanDefinition) -> {
            if (applicationContext.containsBean(beanName)) {
                throw new IllegalArgumentException("Spring context already has a bean named " + beanName);
            }
            registry.registerBeanDefinition(beanName, beanDefinition);
            log.info("Registered RpcReferenceBean {} success.", beanName);
        });
    }

    private void parseRpcReference(Field field) {
        RpcReference rpcReference = field.getAnnotation(RpcReference.class);
        if (rpcReference != null) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RpcReferenceBean.class);
            builder.setInitMethodName("init");
            // 注入RpcReferenceBean的属性
            builder.addPropertyValue("interfaceClass", field.getType());
            builder.addPropertyValue("serviceVersion", rpcReference.version());
            builder.addPropertyValue("registryType", rpcReference.registryType());
            builder.addPropertyValue("registryAddr", rpcReference.registryAddress());
            builder.addPropertyValue("timeout", rpcReference.timeout());

            BeanDefinition beanDefinition = builder.getBeanDefinition();
            rpcReferenceBeanDefinitions.put(field.getName(), beanDefinition);
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

package io.cruii.crpc.common.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author cruii
 * Created on 2022/11/9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Autowired
public @interface RpcReference {
    String version() default "1.0.0";

    String registryType() default "ZOOKEEPER";

    String registryAddress() default "cd58:2181";

    long timeout() default 5000;
}

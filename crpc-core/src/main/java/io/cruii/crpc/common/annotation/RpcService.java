package io.cruii.crpc.common.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author cruii
 * Created on 2022/11/9
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface RpcService {
    Class<?> serviceInterface() default Object.class;

    String version() default "1.0";
}

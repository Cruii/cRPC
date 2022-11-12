package io.cruii.crpc.provider;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author ${USER}
 * Created on ${DATE}
 */
@SpringBootApplication
@EnableConfigurationProperties
public class RpcProviderApplication {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}
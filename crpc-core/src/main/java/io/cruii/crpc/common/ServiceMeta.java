package io.cruii.crpc.common;

import lombok.Data;

/**
 * @author cruii
 * Created on 2022/11/9
 */
@Data
public class ServiceMeta {
    private String name;

    private String address;

    private String version;

    private int port;
}

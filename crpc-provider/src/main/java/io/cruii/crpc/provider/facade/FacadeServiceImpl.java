package io.cruii.crpc.provider.facade;

import io.cruii.crpc.common.annotation.RpcService;
import io.cruii.crpc.facade.FacadeService;

/**
 * @author cruii
 * Created on 2022/11/9
 */
@RpcService(serviceInterface = FacadeService.class, version = "1.0.0")
public class FacadeServiceImpl implements FacadeService {
    @Override
    public String hello(String name) {
        System.out.println(name);
        return "Hello " + name;
    }
}

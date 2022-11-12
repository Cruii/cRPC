package io.cruii.crpc.consumer.controller;

import io.cruii.crpc.common.annotation.RpcReference;
import io.cruii.crpc.facade.FacadeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author cruii
 * Created on 2022/11/9
 */
@RestController
public class FacadeController {

    @RpcReference
    private FacadeService facadeService;

    //@SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection"})
    //public FacadeController(FacadeService facadeService) {
    //    this.facadeService = facadeService;
    //}

    @GetMapping("facade")
    public String facade(String name) {
        System.out.println(facadeService == null);
        return facadeService.hello(name);
    }
}

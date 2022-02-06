package com.clt.service.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author 陈力天
 * @Date 2022/2/4
 */
@SpringBootApplication
@ComponentScan({"com.clt"})
@EnableFeignClients
@EnableDiscoveryClient
public class ServiceCmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceCmsApplication.class, args);
    }
}
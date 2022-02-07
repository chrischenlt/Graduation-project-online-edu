package com.clt.service.ucenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author 陈力天
 * @Date 2022/2/6
 */
@SpringBootApplication
@ComponentScan({"com.clt"})
@EnableDiscoveryClient
public class ServiceUcenterAppliction {

    public static void main(String[] args) {
        SpringApplication.run(ServiceUcenterAppliction.class, args);
    }
}

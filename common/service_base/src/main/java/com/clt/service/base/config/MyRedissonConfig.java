package com.clt.service.base.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author 陈力天
 * @Date 2022/1/7
 */
@Configuration
public class MyRedissonConfig {


    // redission通过redissonClient对象使用 // 如果是多个redis集群，可以配置
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        Config config = new Config();
        // 创建单例模式的配置
        config.useSingleServer().setAddress("redis://192.168.111.101:6379");
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }

}

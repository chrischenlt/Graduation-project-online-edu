package com.clt.service.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author 陈力天
 * @Date 2022/2/7
 */
@Data
@Component
@ConfigurationProperties(prefix="wx.open")
public class UcenterProperties {
    private String appId;
    private String appSecret;
    private String redirectUri;
}
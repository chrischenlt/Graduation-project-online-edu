package com.clt.service.trade.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author 陈力天
 * @Date 2022/2/12
 */
@Data
@Component
@ConfigurationProperties(prefix="weixin.pay")
public class WxPayProperties {
    private String appId;
    private String partner;
    private String partnerKey;
    private String notifyUrl;
}

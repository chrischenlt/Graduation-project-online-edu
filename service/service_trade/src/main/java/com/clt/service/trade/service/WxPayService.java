package com.clt.service.trade.service;

import java.util.Map;

/**
 * @Author 陈力天
 * @Date 2022/2/12
 */
public interface WxPayService {


    Map<String, Object> createNative(String orderNo, String remoteAddr);
}

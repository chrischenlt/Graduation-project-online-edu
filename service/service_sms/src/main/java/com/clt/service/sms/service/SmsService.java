package com.clt.service.sms.service;

/**
 * @Author 陈力天
 * @Date 2022/2/6
 */
public interface SmsService {

    void sendSms(String mobile, String code);
}

package com.clt.service.sms.service.impl;

import com.clt.service.sms.service.SmsService;
import com.clt.service.sms.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author 陈力天
 * @Date 2022/2/6
 */
@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public void sendSms(String mobile, String code) {
        String host = "https://jmsms.market.alicloudapi.com";
        String path = "/sms/send";
        String method = "POST";
        String appcode = "5a5403d8ac57487c8492e7132aa89d5e";

        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
//        String msg = "【创信】你的验证码是："+code+"，3分钟内有效！";
        querys.put("mobile", mobile);
        querys.put("templateId", "M72CB42894");
        querys.put("value", code);
        Map<String, String> bodys = new HashMap<String, String>();
        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

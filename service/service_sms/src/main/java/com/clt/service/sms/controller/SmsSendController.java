package com.clt.service.sms.controller;

import com.clt.common.base.result.R;
import com.clt.common.base.result.ResultCodeEnum;
import com.clt.common.base.util.FormUtils;
import com.clt.common.base.util.RandomUtils;
import com.clt.service.base.exception.MyException;
import com.clt.service.sms.service.SmsService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * @Author 陈力天
 * @Date 2021/9/21
 */
@RestController
@RequestMapping("/api/sms")
@Api(description = "短信管理")
@CrossOrigin //跨域
@Slf4j
public class SmsSendController {

    @Autowired
    private SmsService smsService;
    @Autowired
    private RedisTemplate redisTemplate;


    @GetMapping("send/{mobile}")
    public R sendCode(@RequestParam() String mobile){

        if (StringUtils.isEmpty(mobile) || !FormUtils.isMobile(mobile)) {
            throw new MyException(ResultCodeEnum.LOGIN_PHONE_ERROR);
        }

        String code = RandomUtils.getFourBitRandom();

        redisTemplate.opsForValue().set(mobile, code, 5, TimeUnit.MINUTES);

//        smsService.sendSms(mobile,code);

        return R.ok().message("短信发送成功");
    }






}














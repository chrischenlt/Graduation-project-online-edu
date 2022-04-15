package com.clt.service.ucenter.controller.api;

import com.clt.common.base.result.ResultCodeEnum;
import com.clt.common.base.util.ExceptionUtils;
//import com.clt.common.base.util.HttpClientUtils;
import com.clt.common.base.util.HttpClientUtils;
import com.clt.common.base.util.JwtInfo;
import com.clt.common.base.util.JwtUtils;
import com.clt.service.base.exception.MyException;
import com.clt.service.ucenter.entity.User;
import com.clt.service.ucenter.service.UserService;
import com.clt.service.ucenter.util.UcenterProperties;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author 陈力天
 * @Date 2022/2/7
 */

@Controller//注意这里没有配置 @RestController
@RequestMapping("/api/ucenter/wx")
@Slf4j
public class ApiWxController {

    @Autowired
    private UcenterProperties ucenterProperties;

    @Autowired
    private UserService userService;

    @GetMapping("login")
    public String genQrConnect(HttpSession session){

        //组装url：https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=回调地址&response_type=code&scope=snsapi_login&state=随机数#wechat_redirect
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";

        //将回调url进行转码
        String redirectUri = "";
        try {
            redirectUri = URLEncoder.encode(ucenterProperties.getRedirectUri(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new MyException(ResultCodeEnum.URL_ENCODE_ERROR);
        }

        //生成随机state，防止csrf攻击
        String state = UUID.randomUUID().toString();
        //将state存入session
        session.setAttribute("wx_open_state", state);

        String qrcodeUrl = String.format(
                baseUrl,
                ucenterProperties.getAppId(),
                redirectUri,
                state);


        //跳转到组装的url地址中去
        return "redirect:" + qrcodeUrl;
    }

    @GetMapping("callback")
    public String callback(String code, String state, HttpSession session){

        return userService.loginCallBackWithWechat(code, state, session);
    }
}

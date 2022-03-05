package com.clt.service.ucenter.controller.admin;

import com.clt.common.base.result.R;
import com.clt.service.ucenter.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 陈力天
 * @Date 2022/2/14
 */
@Api(description = "会员管理")
//@CrossOrigin
@RestController
@RequestMapping("/admin/ucenter/member")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "根据日期统计注册人数")
    @GetMapping("count-register-num/{day}")
    public R countRegisterNum(
            @ApiParam(value = "统计日期", required = true)
            @PathVariable String day){

        // todo redis
        Integer num = userService.countRegisterNum(day);
        return R.ok().data("registerNum", num);
    }
}
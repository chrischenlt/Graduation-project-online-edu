package com.clt.service.ucenter.controller.api;


import com.clt.common.base.result.R;
import com.clt.common.base.result.ResultCodeEnum;
import com.clt.common.base.util.JwtInfo;
import com.clt.common.base.util.JwtUtils;
import com.clt.service.base.dto.UserDto;
import com.clt.service.base.exception.MyException;
import com.clt.service.ucenter.entity.vo.LoginVo;
import com.clt.service.ucenter.entity.vo.RegisterVo;
import com.clt.service.ucenter.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author chenlt
 * @since 2022-02-06
 */
@Api(description = "会员管理")

@RestController
@RequestMapping("/api/ucenter/member")
@Slf4j
public class ApiMemberController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "会员注册")
    @PostMapping("register")
    public R register(@RequestBody RegisterVo registerVo){

        userService.register(registerVo);
        return R.ok().message("注册成功");
    }

    @ApiOperation(value = "会员登录")
    @PostMapping("login")
    public R login(@RequestBody LoginVo loginVo) {
        String token = userService.login(loginVo);
        return R.ok().data("token", token).message("登录成功");
    }

    @ApiOperation(value = "根据token获取登录信息")
    @GetMapping("get-login-info")
    public R getLoginInfo(HttpServletRequest request){

        try {
            JwtInfo jwtInfo = JwtUtils.getUserIdByJwtToken(request);
            return R.ok().data("userInfo", jwtInfo);
        } catch (Exception e) {
            log.error("解析用户信息失败：" + e.getMessage());
            throw new MyException(ResultCodeEnum.FETCH_USERINFO_ERROR);
        }
    }

    @ApiOperation("根据会员id查询会员信息")
    @GetMapping("inner/get-member-dto/{memberId}")
    public UserDto getUserDtoByMemberId(
            @ApiParam(value = "会员ID", required = true)
            @PathVariable String userId){
        UserDto UserDto = userService.getUserDtoByUserId(userId);
        return UserDto;
    }

}
package com.clt.service.ucenter.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.clt.common.base.result.R;
import com.clt.service.ucenter.entity.User;
import com.clt.service.ucenter.entity.vo.UserQueryVo;
import com.clt.service.ucenter.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @ApiOperation("用户分页列表")
    @GetMapping("list/{page}/{limit}")
    public R listPage(@ApiParam(value = "当前页码", required = true) @PathVariable Long page,
                      @ApiParam(value = "每页记录数", required = true) @PathVariable Long limit,
                      @ApiParam("用户列表查询对象") UserQueryVo userQueryVo){

        IPage<User> pageModel = userService.selectPage(page, limit, userQueryVo);
        List<User> records = pageModel.getRecords();
        long total = pageModel.getTotal();
        return R.ok().data("total", total).data("rows", records);
    }

    @PostMapping("forbidden/user/{userId}")
    public R forbiddenUserAccount(@PathVariable String userId) {
        userService.forbiddenUserAccount(userId);
        return R.ok().message("账号禁用成功");
    }

    @PostMapping("enable/user/{userId}")
    public R enableUserAccount(@PathVariable String userId) {
        userService.enableUserAccount(userId);
        return R.ok().message("账号启用成功");
    }

}
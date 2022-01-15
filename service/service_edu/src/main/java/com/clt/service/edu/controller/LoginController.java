package com.clt.service.edu.controller;

import com.clt.common.base.result.R;
import org.springframework.web.bind.annotation.*;

/**
 * @Author 陈力天
 * @Date 2022/1/13
 */
@CrossOrigin
@RestController
@RequestMapping("/user")
public class LoginController {


    @PostMapping("login")
    public R login() {
        return R.ok().data("token", "admin");
    }


    @GetMapping("info")
    public R info() {
        return R.ok()
                .data("name", "admin")
                .data("roles", "[admin]")
                .data("avatar", "头像url");
    }


    @PostMapping("logout")
    public R logout() {
        return R.ok();
    }


}

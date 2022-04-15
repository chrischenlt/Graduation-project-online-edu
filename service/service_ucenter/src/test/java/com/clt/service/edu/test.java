package com.clt.service.edu;

import com.clt.service.ucenter.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @Author 陈力天
 * @Date 2022/4/14
 */
@SpringBootTest
public class test {

    @Resource
    private UserService userService;


    @Test
    public void test1() {
        Long userLoginTime = userService.getUserLoginTime("1491038699188744193");
        System.out.println();
    }
}

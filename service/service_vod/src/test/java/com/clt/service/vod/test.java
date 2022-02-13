package com.clt.service.vod;

import com.clt.service.vod.service.VideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

/**
 * @Author 陈力天
 * @Date 2022/2/13
 */
@SpringBootTest
public class test {

    @Autowired
    private VideoService videoService;

    @Test
    public void test() {
        videoService.removeVideoByIdList(Arrays.asList("8ec1ea3ec3a44ec4afd4756955c8cd0b"));
    }


}

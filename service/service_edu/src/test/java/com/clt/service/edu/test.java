package com.clt.service.edu;

import com.clt.service.edu.entity.Teacher;
import com.clt.service.edu.service.TeacherService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * @Author 陈力天
 * @Date 2022/1/6
 */
@SpringBootTest
public class test {


    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private TeacherService teacherService;

    @Test
    public void test() {
        Teacher teacher = new Teacher();
        teacher.setCareer("niubi");
        teacher.setName("clt");
        teacherService.save(teacher);
    }
}

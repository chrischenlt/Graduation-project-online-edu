package com.clt.service.edu;

import com.clt.service.edu.entity.Course;
import com.clt.service.edu.entity.Teacher;
import com.clt.service.edu.entity.Video;
import com.clt.service.edu.entity.form.CourseInfoForm;
import com.clt.service.edu.mapper.CourseMapper;
import com.clt.service.edu.mapper.VideoMapper;
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
    private VideoMapper videoMapper;
    @Resource
    private CourseMapper courseMapper;

    @Test
    public void test() {

        Course course = new Course();
        course.setId("1487324603557613569");
        course.setViewCount(2L);
        courseMapper.updateById(course);

    }
}

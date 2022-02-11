package com.clt.service.edu;

import com.clt.service.edu.entity.Course;
import com.clt.service.edu.entity.Teacher;
import com.clt.service.edu.entity.Video;
import com.clt.service.edu.entity.form.CourseInfoForm;
import com.clt.service.edu.mapper.CourseMapper;
import com.clt.service.edu.mapper.VideoMapper;
import com.clt.service.edu.service.TeacherService;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

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
    public void test() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("pg");
        producer.setNamesrvAddr("192.168.111.101:9876");
        producer.start();
        for (int i = 0; i < 100; i++) {
            byte[] body = ("Hi," + i).getBytes();
            try {
                Message message = new Message("myTopic", "myTag", body);
                producer.send(message, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        System.out.println(sendResult);
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            }catch (Exception e) {
                e.printStackTrace();
            }

        }
        TimeUnit.SECONDS.sleep(3);
        producer.shutdown();

    }
}

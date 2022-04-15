package com.clt.service.edu;

import com.clt.common.base.util.RocketMQUtils;
import com.clt.service.edu.entity.Course;
import com.clt.service.edu.entity.Teacher;
import com.clt.service.edu.entity.Video;
import com.clt.service.edu.entity.form.CourseInfoForm;
import com.clt.service.edu.mapper.CourseMapper;
import com.clt.service.edu.mapper.VideoMapper;
import com.clt.service.edu.service.CourseCollectService;
import com.clt.service.edu.service.TeacherService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import javax.annotation.Resource;
import java.util.List;
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
    @Resource
    private CourseCollectService courseCollectService;

    @Test
    public void test1() throws Exception {
        System.out.println();

    }

    @Test
    public void rocketmqConsumer() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("cg");
        consumer.setNamesrvAddr("192.168.111.101:9876");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe("myTopic", "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                for (MessageExt msg : list) {
                    System.out.println(msg);
                }
                System.out.println(consumeConcurrentlyContext);
                System.out.println(111);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }

        });

        consumer.start();
        System.out.println("Consumer Started");
        TimeUnit.SECONDS.sleep(15);

    }
}

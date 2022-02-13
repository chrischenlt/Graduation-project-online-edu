package com.clt.common.base.util;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ResourceBundle;

/**
 * @Author 陈力天
 * @Date 2022/2/13
 */
public class RocketMQUtils {

    private static Logger logger = LoggerFactory.getLogger(RocketMQUtils.class);

    //	private static final String ROCKETMQ_TOPIC_TOPIC1 = "topic-1";
//	private static final String ROCKETMQ_TAGS_TAGS1 = "tags-1";
//	private static final String ROCKETMQ_PRODUCEGROUP = "produce_group1";
    private static DefaultMQProducer producer = null;

    static {
        try {

            final String ROCKETMQ_GROUP_NAME = "produce_group";

            final String ROCKETMQ_IP = "192.168.111.101:9876";

            final String ROCKETMQ_RETRY_TIMES = "2";

            final String ROCKETMQ_TIMEOUT = "5000";

            producer = new DefaultMQProducer(ROCKETMQ_GROUP_NAME);

            producer.setNamesrvAddr(ROCKETMQ_IP);
            producer.start();
//             producer.setRetryTimesWhenSendFailed(Integer.valueOf(ROCKETMQ_RETRYTIMES));
            producer.setRetryTimesWhenSendAsyncFailed(Integer.parseInt(ROCKETMQ_RETRY_TIMES));
            producer.setSendMsgTimeout(Integer.parseInt(ROCKETMQ_TIMEOUT));

            logger.info("producer init success >>>");
//			jedisPool = RedisConfig.getInstance().getJedisPool();
        } catch (Exception e) {
            logger.error("producer init fail!!!", e);
        }
    }

    public static <T> SendResult asyncPush(String topic, T data) {
        return asyncPush(topic, null, null, data);
    }

    /**
     * 异步发送消息
     */
    public static <T> SendResult asyncPush(String topic, String tags, String keys, T data) {

        // Create a message instance, specifying topic, tag and message body.
        Message msg = new Message(topic, tags, keys, JSON.toJSONBytes(data));
        try {
            SendResult sendResult = producer.send(msg);
            logger.info("send success>>>:");
            return sendResult;
        } catch (Exception e) {
            logger.error("send error>>>:", e);
            throw new RuntimeException(e);
        }
    }


    public static <T> void syncPush(String topic, List<T> objects) {
        syncPush(topic, null, null, objects);
    }


    /**
     * 同步发送消息
     */
    public static <T> void syncPush(String topic, String tags, String keys, List<T> objects) {

//		Integer retLength = 0;// 消息推送成功 总数
        for (T object : objects) {
            Message msg = new Message(topic, tags, keys, JSON.toJSONBytes(object));
            try {
                SendResult sendResult = producer.send(msg);
                logger.info("send success>>>:" + sendResult.getMsgId());
//				retLength++;
            } catch (Exception e) {
                logger.info("send error>>>:", e);
            }
        }

        //return retLength;
    }

    /**
     * 同步发送消息
     */
    public static <T> void onewayPush(String topic, String tags, String keys, List<T> objects) {
        for (T object : objects) {
            Message msg = new Message(topic, tags, keys, JSON.toJSONBytes(object));
            try {
                producer.sendOneway(msg);
                logger.info("send success>>>:");
            } catch (Exception e) {
                logger.info("send error>>>:", e);
            }
        }

//        objects.stream().forEachOrdered(p->{
//            Message msg = new Message(topic, tags, keys, JSON.toJSONBytes(p));
//            try {
//                producer.sendOneway(msg);
//                logger.info("send success>>>:");
//            } catch (Exception e) {
//                logger.info("send error>>>:", e);
//            }
//        });

    }


}

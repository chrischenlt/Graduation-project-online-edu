package com.clt.service.vod.mq;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.clt.service.vod.util.AliyunVodSDKUtils;
import com.clt.service.vod.util.VodProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Author 陈力天
 * @Date 2022/2/13
 */
@Component
@RocketMQMessageListener(topic = "RemoveVODTopic", consumerGroup = "RemoveVODGroup")
@Slf4j
public class RemoveVideoRequestListener implements RocketMQListener<String> {

    @Autowired
    private VodProperties vodProperties;

    @Override
    public void onMessage(String msgBody) {
        DeleteVideoRequest request = new DeleteVideoRequest();
        DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                vodProperties.getKeyid(),
                vodProperties.getKeysecret());
        try {
            request.setVideoIds(msgBody.substring(1, msgBody.length() - 1));
            client.getAcsResponse(request);
            log.info("VOD删除成功，msgBody : {}", msgBody);
        } catch (Exception e) {
            log.error("VOD删除失败，msgBody : {}", msgBody);
        }
    }

}

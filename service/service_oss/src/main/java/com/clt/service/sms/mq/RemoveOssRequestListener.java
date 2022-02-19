package com.clt.service.sms.mq;

import com.clt.service.sms.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author 陈力天
 * @Date 2022/2/19
 */
@Component
@RocketMQMessageListener(topic = "RemoveOssAvatarTopic", consumerGroup = "RemoveOssAvatarGroup")
@Slf4j
public class RemoveOssRequestListener implements RocketMQListener<String> {

    @Autowired
    private FileService fileService;

    @Override
    public void onMessage(String msgBody) {
        log.info("remove Oss avatar , msgBody = {}", msgBody);
        fileService.removeFile(msgBody.substring(1,msgBody.length() - 1));
    }
}

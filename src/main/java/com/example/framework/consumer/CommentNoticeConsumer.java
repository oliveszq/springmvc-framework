package com.example.framework.consumer;

import com.alibaba.fastjson.JSON;
import com.example.framework.model.dto.EmailDTO;
import com.example.framework.utils.EmailUtil;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.example.framework.constant.RabbitMQConstant.EMAIL_QUEUE;

//指定监听的队列名
@Component
@RabbitListener(queues = EMAIL_QUEUE)
public class CommentNoticeConsumer {
    @Autowired
    private EmailUtil emailUtil;
    /**
     * 使用 @RabbitHandler 注解进行标记，表示此方法用于处理接收到的消息。
     * @param data
     */
    @RabbitHandler
    public void process(byte[] data) {
        EmailDTO emailDTO = JSON.parseObject(new String(data), EmailDTO.class);
        emailUtil.sendHtmlMail(emailDTO);
    }
}

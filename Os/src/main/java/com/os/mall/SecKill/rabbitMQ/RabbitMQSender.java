package com.os.mall.SecKill.rabbitMQ;

import com.os.mall.SecKill.redis.RedisService1;
import com.os.mall.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RabbitMQSender {

    @Autowired
    AmqpTemplate amqpTemplate;

    public static Logger logger=LoggerFactory.getLogger(RabbitMQSender.class);
//    public void sendMsg(Object message){
//        String msg= RedisService.beanToString(message);
//        logger.info("《《《《《发送消息："+message);
//        amqpTemplate.convertAndSend(RabbitMQConfig.QUEUE,msg);      //把msg加入到queue中
//
//    }

    public void sendSecKill(SecKillMsg secKillMsg) {
        // 设置订单创建时间
        secKillMsg.setCreateTime(new Date());
        String msg= RedisService1.beanToString(secKillMsg);
        logger.info("《《《《《发送消息："+msg );


//        // 计算消息的过期时间，这里使用订单创建时间加上15分钟作为过期时间
//        Date expirationTime = new Date(secKillMsg.getCreateTime().getTime() + 15 * 60 * 1000);
//        // 发送消息到延迟队列，并设置消息的过期时间
//        amqpTemplate.convertAndSend(RabbitMQConfig.SecKillQUEUE, msg, message -> {
//            MessageProperties properties = message.getMessageProperties();
//            properties.setExpiration(String.valueOf(expirationTime.getTime()));
//            return message;
//        });

        ///////////
        amqpTemplate.convertAndSend(RabbitMQConfig.SecKillQUEUE,msg);      //把msg加入到queue中

    }


    public void sendOrderToDelayQueue(OrderDelayMsg orderDelayMsg) {
        // 序列化订单信息
        String message=RedisService1.beanToString(orderDelayMsg);
        logger.info("发送订单到延迟队列：" + message);

        // 设置消息的过期时间为15分钟
        long delayTime = 15 * 60 * 1000; // 15分钟的毫秒数
        amqpTemplate.convertAndSend(RabbitMQConfig.ORDER_DELAY_QUEUE, message, m -> {
            m.getMessageProperties().setExpiration(String.valueOf(delayTime));
            return m;
        });
    }

    // 这是将订单对象转换为字符串的示例方法
    private String convertOrderToString(Order order) {

        return "";
    }

}

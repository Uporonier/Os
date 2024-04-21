package com.os.mall.SecKill.rabbitMQ;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE ="queue";
    //秒杀功能的queue
    public static final String SecKillQUEUE ="seckill.queue";
    //秒杀功能的queue
    public static final String DELAYQUEUE ="delay.queue";

    // 死信交换机
    public static final String DLX_EXCHANGE = "dlx.exchange";
    // 死信队列
    public static final String DEAD_LETTER_QUEUE = "dead.letter.queue";

    // 定义延迟队列的名称
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";

    // 定义死信队列的路由键
    public static final String DEAD_LETTER_ROUTING_KEY = "dead.letter.routing.key";

    @Bean
    public Queue queue(){
        return new Queue(QUEUE,true);
    }

    // 在RabbitMQConfig类中添加延迟队列和死信队列的定义
// 创建一个延迟队列，用于存放订单信息，设置其消息过期后转发到的死信交换机和路由键
    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> args = new HashMap<>();
        // 指定消息过期后转发的交换机
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        // 指定消息过期后转发的路由键
        args.put("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY);
        // 创建队列，启用持久化
        return new Queue(ORDER_DELAY_QUEUE, true, false, false, args);
    }

    // 创建一个死信队列，用于接收过期的订单消息
    @Bean
    public Queue deadLetterQueue() {
        // 创建队列，启用持久化
        return new Queue(DEAD_LETTER_QUEUE, true);
    }

    // 创建一个主题交换机，用于处理延迟队列中过期的消息
    @Bean
    public TopicExchange dlxExchange() {
        // 创建主题交换机
        return new TopicExchange(DLX_EXCHANGE);
    }

    // 创建一个绑定，将死信队列绑定到死信交换机
    @Bean
    public Binding DLQBinding() {
        // 将死信队列绑定到死信交换机，使用路由键 DEAD_LETTER_ROUTING_KEY
        return BindingBuilder.bind(deadLetterQueue()).to(dlxExchange()).with(DEAD_LETTER_ROUTING_KEY);
    }




}

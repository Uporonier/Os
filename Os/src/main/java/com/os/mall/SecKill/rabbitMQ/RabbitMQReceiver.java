package com.os.mall.SecKill.rabbitMQ;

import com.os.mall.SecKill.redis.RedisService1;
import com.os.mall.entity.User;
import com.os.mall.service.OrderService;
import com.os.mall.SecKill.recParam.SecKillGoodsRP;
import com.os.mall.SecKill.redis.GoodsKey;
import com.os.mall.SecKill.redis.OrderStatusKey;
import com.os.mall.SecKill.service.GoodsService;
import com.os.mall.SecKill.service.SecKillOrderService;
import com.os.mall.SecKill.service.SecKillService;
import com.os.mall.entity.Order;
import com.os.mall.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQReceiver {

    @Autowired
    GoodsService goodsService;
    @Autowired
    RedisService1 redisService1;
    @Autowired
    SecKillOrderService secKillOrderService;
    @Autowired
    SecKillService secKillService;
    @Autowired
    RedisService1 redisService;
    @Autowired
    CartService cartService;
    @Autowired
    OrderService orderService;
    @Autowired
    AmqpTemplate amqpTemplate;
    private static Logger logger= LoggerFactory.getLogger(RabbitMQReceiver.class);


    @RabbitListener(queues = RabbitMQConfig.SecKillQUEUE)
    public void receive(String message){
        logger.info("》》》》》收到消息："+message);
        //string转bean
        SecKillMsg secKillMsg= RedisService1.stringToBean(message, SecKillMsg.class);
        //获取对应的user跟goodsid
        User user1 =secKillMsg.getUser();
        long goodsId= secKillMsg.getGoodsId();


        //判断商品是否还有库存
//        GoodsRP goods=goodsService.getGoodsRPByGoodsId(goodsId);
        SecKillGoodsRP secKillGoodsRP=goodsService.getSecKillGoodsRPByGoodsId(goodsId);
        int num=secKillGoodsRP.getStockCount();
        if(num<=0) {

            return ;
        }

/***************************这地方把cart改回order***********************************/
        //确定是否秒杀成功过生成订单  改成  确认是否生成过cart
//        Cart cart=cartService.getSecKillOrderByUserIdGoodsId(user1.getId(),goodsId);
////        SecKillOrder secKillOrder= secKillOrderService.getSecKillOrderByUserIdGoodsId(user1.getId(),goodsId);
//        if(cart!=null){
//            return ;
//        }
        // 从redis中查看是否生成过order
        Order order=orderService.getSecKillOrderByUserIdGoodsId(user1.getId(),goodsId);
        if (order!=null){
            return;
        }
        /***********************************/

        //生成秒杀订单---改成生成秒杀cart
        secKillService.secKill(user1,secKillGoodsRP);

    }

    @RabbitListener(queues = RabbitMQConfig.DEAD_LETTER_QUEUE)
    public void receiveDeadLetterQueueMessages(String message) {
        // 假设消息内容是订单ID
        System.out.println("Received message from dead letter queue: " + message);

        try {
            // 进行业务处理，例如检查订单状态并更新为"已过期"
             // 获取订单信息
            OrderDelayMsg orderDelayMsg=redisService1.stringToBean(message,OrderDelayMsg.class);
            String orderNo=orderDelayMsg.getOrderNo();
            int goodsId=orderDelayMsg.getGoodsId();
            //从redis中读出当前订单的状态  判断当前状态是否已经支付   延时队列作用是延时15分钟进行判断
            String status=redisService1.get(OrderStatusKey.getSecKillOrderStatusByOrderNo,orderNo,String.class);
            System.out.println("redis中的状态为"+status);
            if("待付款".equals(status)) {
                System.out.println("从redis获取到订单" + orderNo + "未付款，订单失效");
                Order order=orderService.getByOrderNo(orderNo);
                order.setState("已失效");
                //设置redis中状态为已失效  已经出队  不设置也行  优化时候可以注释掉
                redisService1.set(OrderStatusKey.getSecKillOrderStatusByOrderNo,orderNo,order.getState());
                //更新数据库中订单的状态为失效
                orderService.updateById(order);
                /********************************************/
                //回仓处理  redis中对应的商品stock+1
                String goodsId1=String.valueOf(goodsId);
                String stock=redisService1.get(GoodsKey.getSecKillGoodsStock,goodsId1,String.class);
                int stock1=Integer.valueOf(stock);
                redisService1.set(GoodsKey.getSecKillGoodsStock,goodsId1,stock1+1);
                //数据库库存+1
                goodsService.addStock1(Long.parseLong(goodsId1));
            }

        } catch (Exception e) {
            System.err.println("Error processing message from dead letter queue: " + e.getMessage());
            // 处理错误情况
        }
    }






}

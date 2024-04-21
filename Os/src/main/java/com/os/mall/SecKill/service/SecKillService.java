package com.os.mall.SecKill.service;

import com.os.mall.SecKill.rabbitMQ.RabbitMQSender;
import com.os.mall.SecKill.recParam.SecKillGoodsRP;
import com.os.mall.SecKill.redis.OrderKey;
import com.os.mall.SecKill.redis.OrderStatusKey;
import com.os.mall.SecKill.redis.RedisService1;
import com.os.mall.SecKill.redis.SecKillKey;
import com.os.mall.entity.Order;
import com.os.mall.entity.User;
import com.os.mall.SecKill.rabbitMQ.OrderDelayMsg;
import com.os.mall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SecKillService {
    @Autowired
    GoodsService goodsService;
    @Autowired
    SecKillOrderService secKillOrderService;
    @Autowired
    CartService cartService;
    /*
    @Transactional
    public Order1 secKill1(User user1, GoodsRP goods) {//原子操作
        //减少库存
        if(goodsService.reduceStock(goods)){
            //  写订单  ！！！！减库存失败的时候不能写订单
            System.out.println("rabbitMQReceiver的seckill ----1");
            return secKillOrderService.createOrder(user1,goods);
        }
        else{
            System.out.println("rabbitMQReceiver的seckill----2");
            setSecOver(goods.getId());
            return null;
        }
    }

     */

    @Autowired
    RabbitMQSender rabbitMQSender;
    @Transactional
    public Order secKill(User user1, SecKillGoodsRP goods) {//原子操作
        System.out.println("进入了seckill");

       

        //减少库存
        if(goodsService.reduceStock1(goods)){
            //  写订单  ！！！！减库存失败的时候不能写订单
            System.out.println("rabbitMQReceiver的seckill ----1");
            Order order=secKillOrderService.createSecOrder(user1,goods);

            //发送到延时队列
            OrderDelayMsg orderDelayMsg=new OrderDelayMsg();
            orderDelayMsg.setOrderNo(order.getOrderNo());
            orderDelayMsg.setGoodsId(Integer.parseInt(goods.getId().toString()));
            rabbitMQSender.sendOrderToDelayQueue(orderDelayMsg);
            return order;
        }
        else{
            System.out.println("rabbitMQReceiver的seckill----2");
            setSecOver(goods.getId());
            return null;
        }
    }

    @Autowired
    RedisService1 redisService1;
    private void setSecOver(Long id) {
        redisService1.set(SecKillKey.goodsOver,""+id,true);

    }
    private boolean getSecOver(long goodsId) {
        return redisService1.exists(SecKillKey.goodsOver,""+goodsId);
    }

    //获取秒杀结果
    public String getres(long id, long goodsId) {
//    SecKillOrder secKillOrder= secKillOrderService.getSecKillOrderByUserIdGoodsId(id,goodsId);

//    Cart cart=cartService.getSecKillOrderByUserIdGoodsId(Integer.valueOf((int) id),goodsId);
    Order order=secKillOrderService.getSecKillOrderByUserIdGoodsId(Integer.valueOf((int) id),goodsId);
    System.out.println("////////////////////////////////////order是"+order);
    if (order==null)
    {
//        return -1;
        //两种可能  1还没生成订单   2商品全部卖完结束
        if (getSecOver(goodsId)){
            return "-1";//卖完了
        }
        else
            return "0";//还没有卖完 继续轮询
    }
    else
        return order.getOrderNo();
    }

}

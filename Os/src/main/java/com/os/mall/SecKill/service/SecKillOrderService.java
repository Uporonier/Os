package com.os.mall.SecKill.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.os.mall.SecKill.redis.RedisService1;
import com.os.mall.entity.Address;
import com.os.mall.entity.Order;
import com.os.mall.entity.OrderGoods;
import com.os.mall.entity.User;
import com.os.mall.service.OrderService;
import com.os.mall.SecKill.dao.OrderDao;
import com.os.mall.SecKill.entity.Order1;
import com.os.mall.SecKill.recParam.SecKillGoodsRP;
import com.os.mall.SecKill.redis.OrderKey;
import com.os.mall.SecKill.redis.OrderStatusKey;
import com.os.mall.entity.*;
import com.os.mall.mapper.OrderGoodsMapper;
import com.os.mall.mapper.OrderMapper;
import com.os.mall.service.AddressService;
import com.os.mall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class SecKillOrderService {
    @Autowired
    RedisService1 redisService1;
    @Autowired
    OrderDao orderDao;
    @Autowired
    OrderService orderService;
    public Order getSecKillOrderByUserIdGoodsId(long userId, long goodsId) {
//        return orderDao.getSecKillOrderByUserIdGoodsId(userId,goodsId);
//      直接从缓存中查找
        return redisService1.get(OrderKey.getSecKillOrderByUserIdwithGoodsid,
                "-"+userId+"-"+goodsId,Order.class);
//  SecKillOrder_Uid_Gid-18769808523-1      用户18769808523   1号商品

    }
/*
    @Transactional
    public Order1 createOrder(User user1, GoodsRP goods) {  //创建订单
        /*
        System.out.println("调用了rabbitMQReceiver的createOrder方法");
        Order1 order1 =new Order1();
        order1.setCreateDate(new Date());
        order1.setDeliveryAddrId(0L);
        order1.setGoodsCount(1);
        order1.setGoodsId(goods.getId());
        order1.setGoodsName(goods.getGoodsName());
        order1.setGoodsPrice((double)goods.getSeckillPrice());
        order1.setOrderChannel(1);
        order1.setStatus(0); //0是新建
        order1.setUserId(Long.parseLong((user1.getId()).toString()));
        orderDao.insert(order1);//???????
        System.out.println("id号是"+ order1.getId());
        SecKillOrder secKillOrder=new SecKillOrder();
        secKillOrder.setGoodsId(goods.getId());
        secKillOrder.setOrderId(order1.getId());
        secKillOrder.setUserId(Long.parseLong((user1.getId()).toString()));
        orderDao.insertSecKillOrder(secKillOrder);

        //secKillOrder对象放到redis
        redisService1.set(OrderKey.getSecKillOrderByUserIdwithGoodsid,
                "-"+ user1.getId()+"-"+goods.getId(),secKillOrder);

        return order1;

    }
    */

    @Autowired
    CartService cartService;
    @Autowired
    AddressService addressService;
    @Autowired
    GoodsService goodsService;
    public String generateOrderNumber() {
        // 获取当前日期和时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());

        // 生成一个随机数
        Random random = new Random();
        int endRandom = random.nextInt(999999); // 生成六位随机数
        // 确保随机数为6位
        String strRandom = String.format("%06d", endRandom);

        // 组合为订单号
        String orderNumber = timestamp + strRandom;
        return orderNumber;
    }
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderGoodsMapper orderGoodsMapper;
    @Transactional
    public Order createSecOrder(User user1, SecKillGoodsRP goods) {
        //创建订单  创建购物车表中数据
        System.out.println("调用了rabbitMQReceiver的createSecOrder方法");
        Order order=new Order();

        order.setUserId(user1.getId());

        Long userid= Long.parseLong(user1.getId().toString());

        //先把地址设置为该用户的第一个地址
        List<Address> list=addressService.findAllById(userid);
        order.setState("待付款");
        order.setGoodsid(goods.getId());
        BigDecimal seckillPrice = BigDecimal.valueOf(goods.getSeckillPrice());
        order.setTotalPrice(seckillPrice);
        if(list.size()>0)
        {
            order.setLinkAddress(list.get(0).getLinkAddress());
            order.setLinkPhone(list.get(0).getLinkPhone());
            order.setLinkUser(list.get(0).getLinkUser());
        }

        System.out.println("订单号为"+order.getOrderNo());

        System.out.println("saveorder");
//        orderService.saveOrder(order);
        String orderNo = DateUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(6);
        order.setOrderNo(orderNo);
        order.setCreateTime(DateUtil.now());

        //写缓存  不要放在前面  放在前面导致缓存里面信息不全面
        redisService1.set(OrderKey.getSecKillOrderByUserIdwithGoodsid,
                "-"+ user1.getId()+"-"+goods.getId(),order);
        orderMapper.insert(order);

        OrderGoods orderGoods = new OrderGoods();
        orderGoods.setOrderId(order.getId());
        orderGoods.setCount(1);
        orderGoods.setStandard("标准版");
        orderGoods.setGoodId(goods.getId());
        orderGoodsMapper.insert(orderGoods);

        //将订单支付状态写入redis
        redisService1.set(OrderStatusKey.getSecKillOrderStatusByOrderNo,orderNo,order.getState());




        return order;
/**
        Cart cart=new Cart();
        cart.setCount(1);
        cart.setStandard("标准版");
        cart.setCreateTime(DateUtil.now());
        cart.setUserId(Long.parseLong((user1.getId()).toString()));
        cart.setGoodId(goods.getId());
        cart.setIsseckill(1);
        cart.setSecprice(goodsService.getSecPriceBySecGoodsId(Integer.parseInt((goods.getId()).toString())));

        //写入数据库
//        cartService.save(cart);

        //secKillOrder对象放到redis
        //注意重复秒杀
        redisService1.set(OrderKey.getSecKillOrderByUserIdwithGoodsid,
                "-"+ user1.getId()+"-"+goods.getId(),cart);
        return cart;
**/

    }

    public Order1 getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }


}

package com.os.mall.service;

import com.os.mall.SecKill.redis.OrderKey;
import com.os.mall.SecKill.redis.RedisService1;
import com.os.mall.entity.Cart;
import com.os.mall.mapper.CartMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class CartService extends ServiceImpl<CartMapper, Cart> {

    @Resource
    private CartMapper cartMapper;
    @Autowired
    RedisService1 redisService;
    public List<Map<String,Object>> selectByUserId(Long userId) {
        System.out.println("进了mapper");
        List<Map<String,Object>> list=cartMapper.selectByUserId(userId);
        List<Map<String,Object>> list1=cartMapper.selectSecKillCartByUserId(userId);
        list.addAll(list1);
        return list;
//         return cartMapper.selectByUserId(userId);
    }

    public Cart getByByUserIdGoodsId(Integer userId, long goodsId) {
        return cartMapper.selectByByUserIdGoodsId(userId,goodsId);
    }




    public Cart getSecKillOrderByUserIdGoodsId(long userId, long goodsId) {
//        return orderDao.getSecKillOrderByUserIdGoodsId(userId,goodsId);
//      直接从缓存中查找
        return redisService.get(OrderKey.getSecKillOrderByUserIdwithGoodsid,
                "-"+userId+"-"+goodsId,Cart.class);
//  SecKillOrder_Uid_Gid-18769808523-1      用户18769808523   1号商品

    }


}

package com.os.mall.SecKill.dao;

import com.os.mall.SecKill.entity.Order1;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {
    @Select("select *from sk_order where user_id=#{userId} and goods_id=#{goodsId}")
    Order1 getSecKillOrderByUserIdGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

    Order1 getOrderById(long orderId);
}


    // order_id确定有点问题。。。。    改好了
//    @Insert("insert into sk_order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date) values ("
//            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel}, #{status}, #{createDate})")
//    @Options(useGeneratedKeys = true, keyProperty = "id")
//    long insert(Order order);



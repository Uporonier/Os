package com.os.mall.mapper;

import com.os.mall.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface OrderMapper extends BaseMapper<Order> {
    @Update("update t_order set state = '已支付' where order_no = #{orderNo}")
    void payOrder(String orderNo);

    @MapKey("id")
    List<Map<String, Object>> selectByUserId(int userId);

    @Update("update t_order set state = '已收货' where order_no = #{orderNo}")
    boolean receiveOrder(String orderNo);

    Map<String, Object> selectByOrderNo(String orderNo);
    Map<String, Object> selectByOrderNo1(String orderNo);

    @Select("SELECT * FROM t_order WHERE order_no = #{orderId}")
    Order getByOrderNo(String orderId);
    @Select("SELECT * FROM t_order ")
    List<Order> findAllOrders();
}

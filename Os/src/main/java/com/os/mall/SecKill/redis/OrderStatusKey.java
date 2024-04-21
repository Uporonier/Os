package com.os.mall.SecKill.redis;

public class OrderStatusKey extends  BasePrefix{
    public OrderStatusKey( String Prefixx) {
        super( Prefixx);
    }
    public static OrderKey getSecKillOrderStatusByOrderNo=new OrderKey("SecKillOrderStatus");
}

package com.os.mall.SecKill.redis;

public class OrderKey extends  BasePrefix{
    public OrderKey( String Prefixx) {
        super( Prefixx);
    }
    public static OrderKey getSecKillOrderByUserIdwithGoodsid=new OrderKey("SecKillOrder_Uid_Gid");
}

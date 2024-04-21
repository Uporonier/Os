package com.os.mall.SecKill.redis;

public class SecKillKey extends BasePrefix{
    public SecKillKey( String Prefixx) {
        super(Prefixx);
    }

    //在redis里面写上秒杀商品是不是已经全部卖完了
    public static SecKillKey goodsOver=new SecKillKey("goodsover");
}

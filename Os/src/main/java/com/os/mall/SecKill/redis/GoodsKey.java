package com.os.mall.SecKill.redis;

public class GoodsKey extends BasePrefix{
    private GoodsKey( int befSeconds, String prefix){
        super(befSeconds,prefix);
    }

    //秒杀模块    往redis写
    public static GoodsKey getSecKillGoodsStock=new GoodsKey(0,"goodsstock");
    public static GoodsKey getGoodsList=new GoodsKey(60,"goodslist");  // 页面缓存有效期为60s
    public static GoodsKey getGoodsDetail=new GoodsKey(60,"goodsdetail");
}

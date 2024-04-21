package com.os.mall.SecKill.recParam;

import com.os.mall.SecKill.entity.Order1;

public class OrderRP {  //receive params

    private GoodsRP goodsRP;
    private Order1 order1;

    public GoodsRP getGoodsRP() {
        return goodsRP;
    }

    public void setGoodsRP(GoodsRP goodsRP) {
        this.goodsRP = goodsRP;
    }

    public Order1 getOrder() {
        return order1;
    }

    public void setOrder(Order1 order1) {
        this.order1 = order1;
    }
}

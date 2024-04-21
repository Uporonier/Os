package com.os.mall.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("userevents")
public class UserEvents {
    public Integer userid;
    private Long goodsid;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Long getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(Long goodsid) {
        this.goodsid = goodsid;
    }

    @Override
    public String toString() {
        return "UserEvents{" +
                "userid=" + userid +
                ", goodsid=" + goodsid +
                '}';
    }
}

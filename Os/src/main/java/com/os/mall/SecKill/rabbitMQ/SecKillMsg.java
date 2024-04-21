package com.os.mall.SecKill.rabbitMQ;

import com.os.mall.entity.User;

import java.util.Date;

public class SecKillMsg {

    private User user1;
    private long goodsId;
    private Date createTime;

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public User getUser() {
        return user1;
    }

    public void setUser(User user1) {
        this.user1 = user1;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

}

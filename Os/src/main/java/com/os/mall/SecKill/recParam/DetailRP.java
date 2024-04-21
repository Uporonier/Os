package com.os.mall.SecKill.recParam;

import com.os.mall.SecKill.entity.User1;

public class DetailRP {
    private User1 user1;
    private GoodsRP goodsRP;
    private int secKillStatus = 0;  //未开始-1 开始0 结束1
    private int remainTime = 0;   //开始倒计时
    public GoodsRP getGoodsRP() {
        return goodsRP;
    }

    public User1 getUser() {
        return user1;
    }

    public void setUser(User1 user1) {
        this.user1 = user1;
    }

    public void setGoodsRP(GoodsRP goodsRP) {
        this.goodsRP = goodsRP;
    }


    public int getSecKillStatus() {
        return secKillStatus;
    }

    public void setSecKillStatus(int secKillStatus) {
        this.secKillStatus = secKillStatus;
    }

    public int getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(int remainTime) {
        this.remainTime = remainTime;
    }

}

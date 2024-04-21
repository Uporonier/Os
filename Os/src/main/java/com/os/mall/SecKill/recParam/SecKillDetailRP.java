package com.os.mall.SecKill.recParam;

import com.os.mall.entity.User;

public class SecKillDetailRP {
    private User user;
    private SecKillGoodsRP secKillGoodsRP;
    private int secKillStatus = 0;  //未开始-1 开始0 结束1
    private int remainTime = 0;   //开始倒计时

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public SecKillGoodsRP getSecKillGoodsRP() {
        return secKillGoodsRP;
    }

    public void setSecKillGoodsRP(SecKillGoodsRP secKillGoodsRP) {
        this.secKillGoodsRP = secKillGoodsRP;
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

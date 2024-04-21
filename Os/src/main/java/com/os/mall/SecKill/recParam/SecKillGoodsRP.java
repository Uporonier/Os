package com.os.mall.SecKill.recParam;

import com.os.mall.entity.Good;

import java.util.Date;

public class SecKillGoodsRP extends Good {
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
    private float seckillPrice;

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public float getSeckillPrice() {
        return seckillPrice;
    }

    public void setSeckillPrice(float seckillPrice) {
        this.seckillPrice = seckillPrice;
    }
}

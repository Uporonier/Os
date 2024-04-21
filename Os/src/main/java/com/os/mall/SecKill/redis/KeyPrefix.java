package com.os.mall.SecKill.redis;

public interface KeyPrefix {
    public int befSeconds();//预热期
    public String getPrefix();

}

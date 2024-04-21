package com.os.mall.utils;

import com.os.mall.SecKill.redis.RedisService1;
import com.os.mall.SecKill.redis.UserKey;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

public class SecKillRedis {

    private RedisTemplate<String, Object> redisTemplate;

    public SecKillRedis(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public <T> boolean set(UserKey prefix, String key, T value) {
        try {
            String str = RedisService1.beanToString(value);
            if (str == null || str.length() <= 0) {
                return false;
            }
            String strKey = prefix.getPrefix() + key;
            long sec = prefix.befSeconds();
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            if (sec <= 0) {
                ops.set(strKey, str);
            } else {
                ops.set(strKey, str, sec, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
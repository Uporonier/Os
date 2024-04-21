package com.os.mall.SecKill.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisPoolFactory1 {
    @Autowired
    RedisConfig1 redisConfig1;
    @Bean
    public JedisPool JedisPoolFactory(){
        JedisPoolConfig poolConfig=new JedisPoolConfig();
        poolConfig.setMaxIdle(redisConfig1.getPoolMaxIdle());
        poolConfig.setMaxTotal(redisConfig1.getPoolMaxTotal());
        poolConfig.setMaxWaitMillis(1000* redisConfig1.getPoolMaxWait()); //毫秒变秒
        JedisPool jedisPool1=new JedisPool(poolConfig, redisConfig1.getHost(), redisConfig1.getPort(),
                redisConfig1.getTimeout()*1000, redisConfig1.getPassword(),0);
        return jedisPool1;
    }
}

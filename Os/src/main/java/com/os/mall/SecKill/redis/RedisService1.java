package com.os.mall.SecKill.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service

public class RedisService1 {

    @Autowired
    JedisPool jedisPool;

    public <T> T get(KeyPrefix prefix,String key,Class<T> cla){

        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            String realKey=prefix.getPrefix()+key;
            String strkey=jedis.get(realKey);
            T t= stringToBean(strkey,cla);
            return t;
        }finally {

            returnToPool(jedis);
        }
    }
    public<T> boolean set(KeyPrefix prefix,String key,T value){

        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            String str=beanToString(value);
            System.out.println("秒杀的-------------------------------------------"+str);
            if(str==null||str.length()<=0){
                return false;
            }
            String strKey=prefix.getPrefix()+key;
            int sec =  prefix.befSeconds();
            if(sec<=0){
                jedis.set(strKey,str);
            }else {
                jedis.setex(strKey,sec,str);
            }
            jedis.set(strKey,str);
            return true;
//            return t;
        }finally {

            returnToPool(jedis);
        }
    }


    public boolean delete(KeyPrefix prefix,String key){

        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();

            String strKey=prefix.getPrefix()+key;
            long num=  jedis.del(strKey); //看看是否存在
            return num>0;
//            return t;
        }finally {

            returnToPool(jedis);
        }
    }


    // 判断key是否存在
    public<T> boolean exists(KeyPrefix prefix,String key){

        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();

            String strKey=prefix.getPrefix()+key;
            return  jedis.exists(strKey); //看看是否存在
//            return t;
        }finally {

            returnToPool(jedis);
        }
    }

    //增加值
    public<T> Long incr(KeyPrefix prefix,String key){

        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();

            String strKey=prefix.getPrefix()+key;
            return  jedis.incr(strKey); //看看是否存在
//            return t;
        }finally {

            returnToPool(jedis);
        }
    }

    //减少值
    public<T> Long decr(KeyPrefix prefix,String key){

        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();

            String strKey=prefix.getPrefix()+key;
            return  jedis.decr(strKey); //看看是否存在
//            return t;
        }finally {

            returnToPool(jedis);
        }
    }



    //把Bean对象转换成字符串
    public static <T> String beanToString(T value) {
        if(value==null){
            return null;
        }
        Class<?> cla=value.getClass();
        if(cla==int.class||cla== Integer.class){
            return ""+value;

        }else if (cla==String.class){
            return (String)value;
        }else if (cla==long.class||cla==Long.class){
            return ""+value;
        }
        else {
            return JSON.toJSONString(value);
        }
    }


    public static  <T> T stringToBean(String str,Class<T> cla) {
        if(str==null||str.length()<=0||cla==null){
            return null;
        }

        if(cla==int.class||cla== Integer.class){
            return (T)Integer.valueOf(str);

        }else if (cla==String.class){
            return (T) str;
        }else if (cla==long.class||cla==Long.class){
            return (T)Long.valueOf(str);
        }
        else {
            return JSON.toJavaObject(JSON.parseObject(str),cla);
        }
    }

    private void returnToPool(Jedis jedis) {
        if(jedis!=null)
        {
            jedis.close();
        }
    }


}

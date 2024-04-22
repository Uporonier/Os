package com.os.mall.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.os.mall.SecKill.redis.RedisService1;
import com.os.mall.service.UserService;
import com.os.mall.utils.UserHolder;
import com.os.mall.SecKill.redis.UserKey;
import com.os.mall.constants.Constants;
import com.os.mall.constants.RedisConstants;
import com.os.mall.entity.User;
import com.os.mall.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/*
第一层拦截器，验证用户token,把redis中的user存到threadlocal
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    RedisService1 redisService1;
    @Resource
    RedisTemplate<String, User> redisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取请求的URI
        String requestURI = request.getRequestURI();

        // 检查请求的URI是否是需要跳过的
        if (requestURI.startsWith("/recommend/")) {
            // 如果是，直接返回true放行
            return true;
        }// 检查请求的URI是否是需要跳过的
        if (requestURI.startsWith("/saleadvice/")) {
            // 如果是，直接返回true放行
            return true;
        }
        System.out.println("执行到了JwtInterceptor");
        String token = request.getHeader("token");
        if(token==null)
        {
            // 获取请求中的所有Cookie
            Cookie[] cookies = request.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("token")) {
                        token = cookie.getValue(); // 获取名为 "token" 的Cookie的值
                        // 可以在这里进行你想要的操作
                        System.out.println("Token Cookie Value: " + token);
                        break; // 如果找到了名为 "token" 的Cookie，就结束循环
                    }
                }
            }
        }
        System.out.println("token"+token);
        //如果不是映射到方法，直接通过
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        //
        // 排除 /goods1/toList 路径
        if (request.getRequestURI().equals("/login1/doLogin")) {
            System.out.println("jwt排除login路径");
            return true;
        }
        if (request.getRequestURI().equals("/seckill/doSeckill")) {
            System.out.println("jwt排除seckill路径");
            return true;
        }
        if (request.getRequestURI().equals("recommendations/getSimilarProducts")) {
            System.out.println("jwt排除recommendations/getSimilarProducts路径");
            return true;
        }

        // 判断 handler 是否为 HandlerMethod
        if (handler instanceof HandlerMethod) {

            // 获取处理器的 bean 类型
            Class<?> beanType = ((HandlerMethod) handler).getBeanType();
            if(beanType != null && beanType.getName().startsWith("com.rabbiter.em.SecKill.alipay"))
                return true;
            // 判断 bean 类型是否在指定的 controller 包下
            if (beanType != null && beanType.getName().startsWith("com.rabbiter.em.SecKill.controller")) {
//                System.out.println("是秒杀包的控制器");
                // 如果是，则直接放行

                //保存一下当前的user
                //通过token，将redis中的user存到threadlocal（UserHolder）
//                System.out.println("token"+token);
                User user = redisTemplate.opsForValue().get(RedisConstants.USER_TOKEN_KEY + token);
                UserHolder.saveUser(user);
//                System.out.println("拦截器拦截到当前的user是:"+user);
                //重置过期时间
                redisTemplate.expire(RedisConstants.USER_TOKEN_KEY +token, RedisConstants.USER_TOKEN_TTL, TimeUnit.MINUTES);
                return true;
            }
        }

//        System.out.println(token);
//        System.out.println("执行到了JwtInterceptor111");

        //验证是否有token
        if(!StringUtils.hasLength(token)){
            throw  new ServiceException(Constants.TOKEN_ERROR,"无token信息,请重新登陆");
        }

//        System.out.println("执行到了JwtInterceptor222");
        //通过token，将redis中的user存到threadlocal（UserHolder）
        User user = redisTemplate.opsForValue().get(RedisConstants.USER_TOKEN_KEY + token);
        if (user==null)
        {
            user=redisService1.get(UserKey.token,token,User.class);
            System.out.println(user);
            if(user!=null)      //为秒杀的token放行
                return true;
        }
//        System.out.println("执行到了JwtInterceptor333");
        if(user == null){
            throw  new ServiceException(Constants.TOKEN_ERROR,"token失效,请重新登陆");
        }

//        System.out.println(">>>token"+token);
//        System.out.println(">>>拦截器拦截到当前的user是:"+user);
        UserHolder.saveUser(user);
        //重置过期时间
        redisTemplate.expire(RedisConstants.USER_TOKEN_KEY +token, RedisConstants.USER_TOKEN_TTL, TimeUnit.MINUTES);
        //验证token

//        System.out.println("执行到了JwtInterceptor444");
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getUsername())).build();
        try {
            jwtVerifier.verify(token);
        }catch (JWTVerificationException e){
            throw new ServiceException(Constants.TOKEN_ERROR,"token验证失败，请重新登陆");
        }


        return true;
    }
}

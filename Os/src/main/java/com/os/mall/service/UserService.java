package com.os.mall.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.os.mall.SecKill.redis.RedisService1;
import com.os.mall.SecKill.redis.UserKey;
import com.os.mall.SecKill.util.UUID;
import com.os.mall.common.Result;
import com.os.mall.constants.Constants;
import com.os.mall.constants.RedisConstants;
import com.os.mall.entity.LoginForm;
import com.os.mall.entity.User;
import com.os.mall.entity.dto.UserDTO;
import com.os.mall.exception.ServiceException;
import com.os.mall.mapper.UserMapper;
import com.os.mall.utils.SecKillRedis;
import com.os.mall.utils.TokenUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;


@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    public static final String COOKIE_NAME_TOKEN="token";
    @Resource
    RedisTemplate<String,User> redisTemplate;

    SecKillRedis secKillRedis;
    @Autowired
    RedisService1 redisService1;
    private void addCookie(User user, String token, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest){

        System.out.println(">>>>调用了userservice --addcookie的RedisService的set");
        redisService1.set(UserKey.token,token, user);
        System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
        System.out.println("存入的user为"+user);
        // 获取之前请求中的Cookie数组
        Cookie[] cookies = httpServletRequest.getCookies();

        // 判断是否存在同名的Cookie
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_NAME_TOKEN.equals(cookie.getName())) {
                    // 如果存在同名的Cookie，覆盖原来的Cookie
                    cookie.setValue(token);
                    cookie.setMaxAge(UserKey.token.befSeconds());
                    cookie.setPath("/");
                    httpServletResponse.addCookie(cookie);
                    return;
                }
            }
        }

        // 如果不存在同名的Cookie，创建新的Cookie并添加
        Cookie newCookie = new Cookie(COOKIE_NAME_TOKEN, token);
        newCookie.setMaxAge(UserKey.token.befSeconds());
        newCookie.setPath("/");
        httpServletResponse.addCookie(newCookie);
        System.out.println(">>>userservice 的addcookie完毕");
    }

    public UserDTO login(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, LoginForm loginForm) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",loginForm.getUsername());
        queryWrapper.eq("password",loginForm.getPassword());
        User user = getOne(queryWrapper);
        if(user == null) {
            throw new ServiceException(Constants.CODE_403,"用户名或密码错误");
        }
        System.out.println("前端login11");
        String token = TokenUtils.genToken(user.getId().toString(), user.getUsername());

        System.out.println("前端login22");
        //把用户存到redis中
        System.out.println("存入redis时候key中的token："+token);
        redisTemplate.opsForValue().set(RedisConstants.USER_TOKEN_KEY + token,user);

//        redisTemplate.opsForValue().set(UserKey.token + token,user);

        System.out.println("前端login33");
        //jwt不设置过期时间，只设置redis过期时间。
        redisTemplate.expire(RedisConstants.USER_TOKEN_KEY +token, RedisConstants.USER_TOKEN_TTL, TimeUnit.MINUTES);

        System.out.println("前端login44");
        //把查到的user的一些属性赋值给userDTO
        UserDTO userDTO = BeanUtil.copyProperties(user,UserDTO.class);

        System.out.println("前端login55");
        //设置token
        userDTO.setToken(token);

        //允许请求头带cookie
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        /////
        String token1 = UUID.uuid();
        System.out.println(">>>>ttttttt"+token1);

//        redisTemplate.opsForValue().set(UserKey.token + token1.txt,user);
//        User user1=;
        addCookie(user,token1,httpServletResponse,httpServletRequest);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!11"+user);
//        secKillRedis.set(UserKey.token , token1.txt,user);

        return userDTO;

    }

    public User register(LoginForm loginForm) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",loginForm.getUsername());
        User user = getOne(queryWrapper);
        if(user!=null){
            throw new ServiceException(Constants.CODE_403,"用户名已被使用");
        }else{
            user = new User();
            BeanUtils.copyProperties(loginForm,user);
            user.setNickname("新用户");
            user.setRole("user");
            save(user);
            return user;
        }
    }

    public User getOne(String username){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        return getOne(queryWrapper);
    }
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> lambdaQuery = new LambdaQueryWrapper<>();
        lambdaQuery.eq(User::getUsername, username);
        return this.getOne(lambdaQuery, false); // 'false' 表示不抛出异常
    }
    public Result saveUpdate(User user) {
        if(user.getId() != null) {
            // 修改
            User old = this.baseMapper.selectById(user.getId());
            old.setNickname(ObjectUtils.isEmpty(user.getNickname()) ? old.getNickname() : user.getNickname());
            old.setAvatarUrl(ObjectUtils.isEmpty(user.getAvatarUrl()) ? old.getAvatarUrl() : user.getAvatarUrl());
            old.setRole(ObjectUtils.isEmpty(user.getRole()) ? old.getRole() : user.getRole());
            old.setPhone(ObjectUtils.isEmpty(user.getPhone()) ? old.getPhone() : user.getPhone());
            old.setEmail(ObjectUtils.isEmpty(user.getEmail()) ? old.getEmail() : user.getEmail());
            old.setAddress(ObjectUtils.isEmpty(user.getAddress()) ? old.getAddress() : user.getAddress());
            super.updateById(old);
            return Result.success("修改成功");
        } else {
            // 新增
            if(!ObjectUtils.isEmpty(this.getOne(user.getUsername()))) {
                return Result.error("400", "用户名已存在");
            }
            user.setPassword(user.getNewPassword());
            super.save(user);
            return Result.success("新增成功");
        }
    }

    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    /**
     * 重置密码
     *
     * @param id          用户id
     * @param newPassword 新密码
     */
    public void resetPassword(String id, String newPassword) {
        User user = this.getById(id);
        if(user == null) {
            return;
        }
        user.setPassword(newPassword);
        this.updateById(user);
    }
}

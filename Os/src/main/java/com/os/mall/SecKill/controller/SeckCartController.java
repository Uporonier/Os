package com.os.mall.SecKill.controller;

import cn.hutool.core.date.DateUtil;
import com.os.mall.SecKill.redis.RedisService1;
import com.os.mall.SecKill.redis.UserKey;
import com.os.mall.annotation.Authority;
import com.os.mall.common.Result;
import com.os.mall.entity.AuthorityType;
import com.os.mall.entity.Cart;
import com.os.mall.entity.User;
import com.os.mall.mapper.CartMapper;
import com.os.mall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/SecaddCart")
public class SeckCartController {
    @Autowired
    RedisService1 redisService1;
    @Authority(AuthorityType.noRequire)
    @PostMapping("/addCart") // 将@RequestMapping移动到这里，并删除method属性
    @ResponseBody
    public Result add(@RequestParam(value = "goodsId") String goodsId1, @RequestParam(value = "token") String token, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        System.out.println("运行到了addcart");
        //生成cart对象
        User user=redisService1.get(UserKey.token,token, User.class);
        System.out.println("user是"+user);
        Cart cart=new Cart();
        cart.setCount(1);
        Long goodsId=Long.parseLong(goodsId1);
        cart.setGoodId(goodsId);
        cart.setStandard("标准版");
        cart.setUserId(Long.parseLong((user.getId()).toString()));

        //添加到数据库
        cartService.save(cart);

        return Result.success("成功添加到购物车");
    }

    @PostMapping
    public Result save(@RequestBody Cart cart) {
        cart.setCreateTime(DateUtil.now());
        cartService.saveOrUpdate(cart);
        return Result.success();
    }
    @Resource
    private CartMapper cartMapper;
    @Autowired
    CartService cartService;
    public List<Map<String,Object>> selectByUserId(Long userId) {
        return cartMapper.selectByUserId(userId);
    }

}

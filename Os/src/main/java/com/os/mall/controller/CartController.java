package com.os.mall.controller;

import cn.hutool.core.date.DateUtil;
import com.os.mall.annotation.Authority;
import com.os.mall.common.Result;
import com.os.mall.entity.AuthorityType;
import com.os.mall.entity.Cart;
import com.os.mall.service.CartService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


//购物车

@Authority(AuthorityType.requireLogin)
@RestController
@RequestMapping(value = "/api/cart",method = RequestMethod.POST)
public class CartController {
    @Resource
    private CartService cartService;

//    @Autowired
//    RedisService1 redisService1;
    /*
    查询
    */
    //根据购物车id查询
    @GetMapping("/{id}")
    public Result selectById(@PathVariable Long id) {
        System.out.println("购物车查询");
//        System.out.println("当前userId"+id);
        return Result.success(cartService.getById(id));
    }
    //查找所有用户的购物车
    @GetMapping
    public Result findAll() {
        List<Cart> list = cartService.list();
        return Result.success(list);
    }
    //查找某个用户的购物车
    @GetMapping("/userid/{userId}")
    public Result selectByUserId(@PathVariable Long userId) {
        return Result.success(cartService.selectByUserId(userId)) ;
    }

    /*
    保存
    */
    @PostMapping("/addtocart")
    public Result save(@RequestBody Cart cart) {
        System.out.println("调用了controller--save");
        cart.setCreateTime(DateUtil.now());
        cart.setIsseckill(0);
        cartService.saveOrUpdate(cart);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody Cart cart) {
        cartService.updateById(cart);
        return Result.success();
    }

    /*
    删除
    */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        cartService.removeById(id);
        return Result.success();
    }






}

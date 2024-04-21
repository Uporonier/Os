package com.os.mall.SecKill.controller;

import com.os.mall.SecKill.redis.*;
import com.os.mall.SecKill.service.SecKillOrderService;
import com.os.mall.SecKill.service.User1Service;
import com.os.mall.entity.Order;
import com.os.mall.entity.User;
import com.os.mall.SecKill.Result.CodeMessage;
import com.os.mall.SecKill.Result.Result1;
import com.os.mall.SecKill.entity.User1;
import com.os.mall.SecKill.rabbitMQ.RabbitMQSender;
import com.os.mall.SecKill.rabbitMQ.SecKillMsg;
import com.os.mall.SecKill.recParam.SecKillGoodsRP;
import com.os.mall.SecKill.service.GoodsService;
import com.os.mall.SecKill.service.SecKillService;
import com.os.mall.annotation.Authority;
import com.os.mall.entity.AuthorityType;
import com.os.mall.service.OrderService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@CrossOrigin
@Authority(AuthorityType.noRequire)
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {
    public SecKillController() {
    }

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name","ly+xxy");
        return "hello"; //返回的是一个模板的名称

    }
    @Autowired
    User1Service user1Service;
    @RequestMapping("/ss")
    @ResponseBody
//    bug:访问出错 修正：加一个 @ResponseBody注解
    public Result1<User1> getuser(){
        long tmp=1;
        User1 user1 = user1Service.getById(tmp);
        return Result1.suc(user1);
    }
    @Autowired
    RedisService1 redisService1;

    @RequestMapping("/redis/get")
    @ResponseBody
//    bug:访问出错 修正：加一个 @ResponseBody注解
    public Result1<User1> redisGet(){
        User1 user1 = redisService1.get(UserKey.getById,""+1, User1.class);

        return Result1.suc(user1);
    }

    @Autowired
    GoodsService goodsService;
    @Autowired
    SecKillOrderService secKillOrderService;
    @Autowired
    SecKillService secKillService;
    @Autowired
    RabbitMQSender rabbitMQSender;
    @Autowired
    OrderService orderService;
    @RequestMapping(value = "/getsecres",method = RequestMethod.GET)
    @ResponseBody
    public Result1<String> SecRes(Model model, User user1,HttpServletRequest httpServletRequest, @RequestParam("goodsId") long goodsId) {


        // 获取请求中的所有Cookie
        Cookie[] cookies = httpServletRequest.getCookies();

        String token="";
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
        user1=redisService1.get(UserKey.token,token,User.class);
//        System.out.println("11111111111111111111111111111111111111111111111111");
//        System.out.println(user1);
        model.addAttribute("user", user1);
        //获取秒杀的结果  0是等待中 >0是成功
        if (user1 == null) {
            return Result1.error(CodeMessage.SESSION_FAIL);
        }
        String res=secKillService.getres(user1.getId(),goodsId);
        if(!res.equals("-1")&&!res.equals("0"))
        {   //查询到订单了  看看是否已经支付过了
            String status=redisService1.get(OrderStatusKey.getSecKillOrderStatusByOrderNo,res,String.class);
            if (status.equals("已付款")){
                res="已支付";
            }
            else if(status.equals("已失效")){
                res="已失效";
            }
        }

        return Result1.suc(res);
    }



//    @Autowired
    private RedisTemplate<String, User> redisTemplate;



    @RequestMapping(value = "/doSeckill",method = RequestMethod.POST)
    @ResponseBody
    public Result1<Integer> list(Model model, User user, @RequestParam("goodsId") String goodsId1,
                                 HttpServletRequest httpServletRequest,@RequestParam(value = "token",required = false) String token_url){
        System.out.println("进入了doseckill");

        // 获取请求中的所有Cookie
        Cookie[] cookies = httpServletRequest.getCookies();

        String token=token_url;
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
        long goodsId= Long.parseLong(goodsId1);
        String res=redisService1.get(UserKey.token,token,String.class);
        System.out.println("res是---"+res);
        user=redisService1.get(UserKey.token,token,User.class);

        model.addAttribute("user", user);

        if(user ==null){
            System.out.println("user是null");
            return Result1.error(CodeMessage.SESSION_FAIL);
        }
        System.out.println("验证通过");
        //预减库存
        long newStock= redisService1.decr(GoodsKey.getSecKillGoodsStock,""+goodsId);

        //没有库存了
        if(newStock<0){
            System.out.println("redis没有库存了");
            return Result1.error(CodeMessage.STOCK_NULL);
        }
        System.out.println("redis有库存");
        //有库存 需要查一下是不是已经生成过订单  如果生成过 把redis的加回来
        Order order=redisService1.get(OrderKey.getSecKillOrderByUserIdwithGoodsid,
                "-"+user.getId()+"-"+goodsId,Order.class);
        if (order!=null){
            redisService1.incr(GoodsKey.getSecKillGoodsStock,""+goodsId);
        }




        //有库存 入队列
        SecKillMsg secKillMsg=new SecKillMsg();
        secKillMsg.setUser(user);
        secKillMsg.setGoodsId(goodsId);
        rabbitMQSender.sendSecKill(secKillMsg);

        //返回排队中的状态
        return Result1.suc(0);


/*
        //判断商品是否还有库存
        GoodsRP goods=goodsService.getGoodsRPByGoodsId(goodsId);
//        int num=goods.getGoodsStock();
        int num=goods.getStockCount();
        if(num<=0) {

//            System.out.println("88行");
            return Result.error(CodeMessage.SECKILL_OVER);
        }
        //确定是否秒杀成功生成订单
        SecKillOrder secKillOrder= secKillOrderService.getSecKillOrderByUserIdGoodsId(user.getId(),goodsId);
        if(secKillOrder!=null){

//            System.out.println("94行");
            return Result.error(CodeMessage.SECKILL_OVER);
        }
        // 秒杀  减少库存  写订单
        Order order1=secKillService.secKill(user,goods);
        System.out.println("秒杀成功了");
        return Result.suc(order1);
        */

    }

    //会回调这个接口
    @Override
    public void afterPropertiesSet() throws Exception {
        //目的是每次在项目启动时，首先将秒杀商品的库存量加载到redis中
//    List<GoodsRP> goodsRPList=  goodsService.listGoodsRP();
    List<SecKillGoodsRP> secKillGoodsRPList=goodsService.listSecKillGoodsRP();
    if(secKillGoodsRPList==null)
    {
        return ;
    }
    for (SecKillGoodsRP secKillGoodsRP:secKillGoodsRPList){
        redisService1.set(GoodsKey.getSecKillGoodsStock,""+secKillGoodsRP.getId(),secKillGoodsRP.getStockCount());
    }

    }
}

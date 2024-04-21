package com.os.mall.SecKill.controller;

import com.os.mall.SecKill.Result.CodeMessage;
import com.os.mall.SecKill.Result.Result1;
import com.os.mall.SecKill.entity.Order1;
import com.os.mall.SecKill.entity.User1;
import com.os.mall.SecKill.recParam.GoodsRP;
import com.os.mall.SecKill.recParam.OrderRP;
import com.os.mall.SecKill.service.GoodsService;
import com.os.mall.SecKill.service.SecKillOrderService;
import com.os.mall.SecKill.service.User1Service;
import com.os.mall.entity.AuthorityType;
import com.os.mall.annotation.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@Authority(AuthorityType.noRequire)
@RequestMapping("/order1")
public class Order1Controller {
    @Autowired
    User1Service user1Service;

    @Autowired
    SecKillOrderService secKillOrderService;
    @Autowired
    GoodsService goodsService;
    @RequestMapping(value = "/detail",method = RequestMethod.GET)
    @ResponseBody
    public Result1<OrderRP> GetInfo(Model model, User1 user1, @RequestParam("orderId") long orderId){

        if(user1 ==null){
            return Result1.error(CodeMessage.SESSION_FAIL);
        }
        Order1 order1 =secKillOrderService.getOrderById(orderId);
        if (order1 ==null)
            return Result1.error(CodeMessage.OrderNULL);
        long goodsId= order1.getGoodsId();
        GoodsRP goodsRP=goodsService.getGoodsRPByGoodsId(goodsId);

        OrderRP orderRP=new OrderRP();
        orderRP.setOrder(order1);
        orderRP.setGoodsRP(goodsRP);
        return Result1.suc(orderRP);
//        return Result.suc(user);

    }





}

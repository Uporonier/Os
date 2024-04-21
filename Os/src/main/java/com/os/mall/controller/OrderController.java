package com.os.mall.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.os.mall.common.Result;
import com.os.mall.constants.Constants;
import com.os.mall.entity.AuthorityType;
import com.os.mall.entity.Order;
import com.os.mall.service.OrderService;
import com.os.mall.annotation.Authority;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.xml.internal.fastinfoset.stax.events.Util;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
@Authority(AuthorityType.requireLogin)
@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Resource
    private OrderService orderService;

    @PostMapping("/updateaddress")
    public Result updateAddress(@RequestBody Order updatedOrder, @RequestParam(value = "orderNo") String orderNo) {
        Order order=orderService.getByOrderNo(orderNo);
        System.out.println(orderNo);
        System.out.println("order是》》》》》》》》》"+order);
        System.out.println("updatedOrder》》》》》》》》》"+updatedOrder);
        if(order.getLinkUser()!=null)
        order.setLinkUser(updatedOrder.getLinkUser());
        if(order.getLinkPhone()!=null)
        order.setLinkPhone(updatedOrder.getLinkPhone());
        if(order.getLinkAddress()!=null)
        order.setLinkAddress(updatedOrder.getLinkAddress());

        orderService.updateById(order);
        return Result.success(orderNo);

    }

    /*
    查询
    */
    @GetMapping("/userid/{userid}")
    public Result selectByUserId(@PathVariable int userid) {
        return Result.success(orderService.selectByUserId(userid));
    }
    @GetMapping("/orderNo/{orderNo}")
    public Result selectByOrderNo(@PathVariable String orderNo) {
        return Result.success(orderService.selectByOrderNo1(orderNo));
    }
    @GetMapping
    public Result findAll() {
        List<Order> list = orderService.list();
        return Result.success(list);
    }

    /*
    分页查询
    */
    @GetMapping("/page")
    public Result findPage(@RequestParam int pageNum,
                           @RequestParam int pageSize,
                           String orderNo,String state){
        IPage<Order> orderPage = new Page<>(pageNum,pageSize);
        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
//        orderQueryWrapper.ne("state","待付款");
        if(!Util.isEmptyString(state)){
            orderQueryWrapper.eq("state",state);
        }
        if(!Util.isEmptyString(orderNo)){
            orderQueryWrapper.like("order_no",orderNo);
        }

        orderQueryWrapper.orderByDesc("create_time");

        System.out.println("所有的订单是：");
        for (Order order : orderPage.getRecords()) {
            System.out.println("订单号：" + order.getOrderNo());
            System.out.println("状态：" + order.getState());
            // 打印其他订单信息
        }

//        System.out.println(orderService.page(orderPage,orderQueryWrapper));
        return Result.success(orderService.page(orderPage,orderQueryWrapper));
    }
    /*
    保存
    */
    @PostMapping
    public Result save(@RequestBody Order order,@RequestParam(value = "goodsid") Long goodsid) {
        System.out.println(order.toString());
        String orderNo = orderService.saveOrder(order,goodsid);
        return Result.success(orderNo);

    }
    //支付订单
    @GetMapping("/paid/{orderNo}")
    public Result payOrder(@PathVariable String orderNo){
        orderService.payOrder(orderNo);
        return Result.success();
    }
    //发货
    @Authority(AuthorityType.requireAuthority)
    @GetMapping("/delivery/{orderNo}")
    public Result delivery(@PathVariable String orderNo){
        orderService.delivery(orderNo);
        return Result.success();
    }
    //确认收货
    @GetMapping("/received/{orderNo}")
    public Result receiveOrder(@PathVariable String orderNo){
        if(orderService.receiveOrder(orderNo)){
            return Result.success();
        }
        else {
            return Result.error(Constants.CODE_500,"确认收货失败");
        }
    }

    @PutMapping
    public Result update(@RequestBody Order order) {
        orderService.updateById(order);
        return Result.success();
    }

    /*
    删除
    */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        orderService.removeById(id);
        return Result.success();
    }





}

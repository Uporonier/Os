package com.os.mall.SecKill.alipay;//package com.Os.SecKill.controller;

import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.os.mall.SecKill.redis.OrderStatusKey;
import com.os.mall.SecKill.redis.RedisService1;
import com.os.mall.entity.AuthorityType;
import com.os.mall.entity.Order;
import com.os.mall.mapper.GoodMapper;
import com.os.mall.mapper.OrderMapper;
import com.os.mall.service.OrderService;
import com.os.mall.annotation.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

//@Authority(AuthorityType.noRequire)
@RestController
@RequestMapping("/alipay")
public class AliPayController {

//    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String FORMAT = "JSON";
    private static final String CHARSET = "UTF-8";
    private static final String SIGN_TYPE = "RSA2";

    @Resource
    private AliPayConfig aliPayConfig;
    @Autowired
    OrderService orderService;
    @Autowired
    RedisService1 redisService1;
    @Autowired
    GoodMapper goodMapper;
    @GetMapping("/pay")
    public void pay(HttpServletResponse httpResponse, @RequestParam("orderId") String orderId,
                    @RequestParam("goodsName") String goodsName,@RequestParam("price") String price,@RequestParam(value = "num",required = false) int num) throws Exception {
        System.out.println("num为"+num);
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET, aliPayConfig.getAlipayPublicKey(), SIGN_TYPE);

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        request.setReturnUrl(aliPayConfig.getReturnUrl());

        //这里需要获取秒杀商品的goodsId,商品名称，以及价格

        //    暂时先成功集成了支付宝的沙箱支付    改完前端再补充此处代码

        //
//        String subject = orderId.toString(); // 订单主题
        //////////
        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", orderId);  // 我们自己生成的订单编号
        bizContent.set("total_amount", price);
//        bizContent.set("total_amount",  String.format("%.2f", totalAmount)); // 订单的总金额  !!!保留两位小数
        bizContent.set("subject", "您正在支付商品:");   // 支付的名称
        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY");  // 固定配置
        // 假设你要传递的商品数量是2
        String quantity = String.valueOf(num); // 商品数量
// 将商品数量添加到passback_params
        String passbackParams = URLEncoder.encode("quantity=" + quantity, "UTF-8");
        bizContent.set("passback_params", passbackParams);
        System.out.println(bizContent.toString());
        request.setBizContent(bizContent.toString());
/////////////////
//        request.setBizContent("{\"out_trade_no\":\"" + traceNo + "\","
//                + "\"total_amount\":\"" + totalAmount + "\","
//                + "\"subject\":\"" + subject + "\","
//                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form);
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }


    @Authority(AuthorityType.noRequire)
    @PostMapping("/notify")  // 注意这里必须是POST接口
    public String payNotify(HttpServletRequest request) throws Exception {
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            System.out.println("=========支付宝异步回调========");

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
                // System.out.println(name + " = " + request.getParameter(name));
            }
//            String num1=params.get("passback_params");
//            System.out.println(num1);
            // 在notify方法中
            String passbackParams = request.getParameter("passback_params");
            String quantity = URLDecoder.decode(passbackParams, "UTF-8").split("=")[1];





            String orderId = params.get("out_trade_no");
//            String gmtPayment = params.get("gmt_payment");
//            String alipayTradeNo = params.get("trade_no") ;

            String sign = params.get("sign");
            String content = AlipaySignature.getSignCheckContentV1(params);
            boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, aliPayConfig.getAlipayPublicKey(), "UTF-8"); // 验证签名
            // 支付宝验签
            if (checkSignature) {
                // 验签通过
                System.out.println("交易名称: " + params.get("subject"));
                System.out.println("交易状态: " + params.get("trade_status"));
                System.out.println("支付宝交易凭证号: " + params.get("trade_no"));
                System.out.println("商户订单号: " + params.get("out_trade_no"));
                System.out.println("交易金额: " + params.get("total_amount"));
                System.out.println("买家在支付宝唯一id: " + params.get("buyer_id"));
                System.out.println("买家付款时间: " + params.get("gmt_payment"));
                System.out.println("买家付款金额: " + params.get("buyer_pay_amount"));
                System.out.println("购买数量:"+quantity);
                // 查询订单
//                QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
//                queryWrapper.eq("order_id", outTradeNo);
//                Order orders = ordersMapper.selectOne(queryWrapper);
                Order order=orderService.getByOrderNo(orderId);
                System.out.println("order是"+order);
                if (order != null) {
//                    orders.setAlipayNo(alipayTradeNo);
//                    orders.setPayTime(new Date());
                    order.setState("已支付");
                    System.out.println("???order是是"+order);
                    System.out.println(order);
                    orderService.updateById(order);
//                    orderService.payOrder(order.getOrderNo());
                }

                //设置redis中订单的支付状态为已支付
                System.out.println("设置redis中订单的支付状态为已支付>>>>>订单号"+order.getOrderNo());
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+order.getState());
                redisService1.set(OrderStatusKey.getSecKillOrderStatusByOrderNo,order.getOrderNo(),order.getState());
                redisService1.set(OrderStatusKey.getSecKillOrderStatusByOrderNo,order.getOrderNo(),"已付款");

                //给对应商品加销量和销售额
                goodMapper.saleGood(order.getGoodsid(), Integer.parseInt(quantity), order.getTotalPrice());



            }
        }
        return "success";
    }

}
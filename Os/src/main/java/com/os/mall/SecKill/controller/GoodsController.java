package com.os.mall.SecKill.controller;

import com.os.mall.SecKill.entity.User1;
import com.os.mall.SecKill.recParam.SecKillDetailRP;
import com.os.mall.SecKill.redis.GoodsKey;
import com.os.mall.SecKill.redis.RedisService1;
import com.os.mall.SecKill.service.User1Service;
import com.os.mall.entity.AuthorityType;
import com.os.mall.entity.User;
import com.os.mall.SecKill.Result.Result1;
import com.os.mall.SecKill.entity.SecKillGoods;
import com.os.mall.SecKill.recParam.DetailRP;
import com.os.mall.SecKill.recParam.GoodsRP;
import com.os.mall.SecKill.recParam.SecKillGoodsRP;
import com.os.mall.SecKill.service.GoodsService;
import com.os.mall.annotation.Authority;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
//import org.thymeleaf.spring4.context.SpringWebContext;
//import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@CrossOrigin
@Authority(AuthorityType.noRequire)
@RequestMapping("/goods1")
public class GoodsController {

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;

    //秒杀商品表的service
    @Autowired
    GoodsService goodsService;
    @Autowired
    User1Service user1Service;
    @Autowired
    RedisService1 redisService1;


     public  void addSecGoods(Long goodsId,float price,int stock){
         SecKillGoods secKillGoods=new SecKillGoods();
         secKillGoods.setGoodsId(goodsId);
         secKillGoods.setSeckillPrice(price);
         secKillGoods.setStockCount(stock);
         goodsService.addSecGoods(secKillGoods);
     }
    public  void delSecGoods(Long goodsId){
        goodsService.delSecGoods(goodsId);
    }

     //页面缓存
    @RequestMapping(value = "toList1",produces = "text/html")
    @ResponseBody
    public String toList1(HttpServletRequest request,HttpServletResponse response,Model model, User1 user1){
        model.addAttribute("user", user1);

        System.out.println("goodslist");
        String html = redisService1.get(GoodsKey.getGoodsList,"",String.class); //从缓存中取
        if (!StringUtils.isEmpty(html)) //判断缓存中是否有
        {
            return html;    //缓存有 直接返回
        }
        else        //如果为空 需要手动渲染
        {
            //查询商品列表
            List<GoodsRP> goodsList=goodsService.listGoodsRP();
            model.addAttribute("goodsList",goodsList);


            System.out.println("else这里执行了");
            WebContext  webContext=new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
//            SpringWebContext springWebContext=new SpringWebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap(), applicationContext);
//            System.out.println("67676767");
            html= thymeleafViewResolver.getTemplateEngine().process("goodsList",webContext);
//            html= thymeleafViewResolver.getTemplateEngine().process("goodsList",springWebContext);
            if (!StringUtils.isEmpty(html)){
                redisService1.set(GoodsKey.getGoodsList,"",html);//如果不是空  保存到模板中
            }
//            System.out.println("html为"+html);
            return html;
        }
//        return "goodsList";
    }

    //goodslist 页面 进行页面缓存    sk_goods_seckill sgs left join good
    @RequestMapping(value = "toList",produces = "text/html")
    @ResponseBody
    public String toList(HttpServletRequest request,HttpServletResponse response,Model model, User1 user1){
        model.addAttribute("user", user1);
        System.out.println(">>>goodslist");
        String html = redisService1.get(GoodsKey.getGoodsList,"",String.class); //从缓存中取
        if (!StringUtils.isEmpty(html)) //判断缓存中是否有
        {
            return html;    //缓存有 直接返回
        }
        else        //如果为空 需要手动渲染
        {
            //查询商品列表
            List<SecKillGoodsRP> goodsList=goodsService.listSecKillGoodsRP();
            model.addAttribute("goodsList",goodsList);


            System.out.println("else这里执行了");
            WebContext  webContext=new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
            html= thymeleafViewResolver.getTemplateEngine().process("goodsList",webContext);
            if (!StringUtils.isEmpty(html)){
                redisService1.set(GoodsKey.getGoodsList,"",html);//如果不是空  保存到模板中
            }
            return html;
        }
    }






















    @RequestMapping(value = "toDetail/{goodsId}",produces = "text/html")
    @ResponseBody
    public String detail(HttpServletRequest request, HttpServletResponse response, Model model, User1 user1, @PathVariable("goodsId") long goodsId) {
        model.addAttribute("user", user1);

        String html = redisService1.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class); //从缓存中取
        //key 要加上goodsid对应不同的商品  不同商品对应不同id

        if (!StringUtils.isEmpty(html)) //判断缓存中是否有
        {
            return html;
        } else {  //如果为空  进行手动渲染

            GoodsRP goods = goodsService.getGoodsRPByGoodsId(goodsId);
            model.addAttribute("goods", goods);

            long beginTIme = goods.getStartDate().getTime();
            long endTime = goods.getEndDate().getTime();
            long now = System.currentTimeMillis();
            int secKillStatus = 0;  //未开始-1 开始0 结束1
            int remainTime = 0;   //开始倒计时
            if (now < beginTIme) {
                //未开始 开始倒计时
                secKillStatus = -1;
                remainTime = (int) ((beginTIme - now) / 1000);
            } else if (now > endTime) {
                //秒杀结束
                secKillStatus = 1;
                remainTime = -1;

            } else {
                // 秒杀中
                secKillStatus = 0;
                remainTime = 0;
            }
            model.addAttribute("secKillStatus", secKillStatus);
            model.addAttribute("remainTime", remainTime);


//            SpringWebContext springWebContext=new SpringWebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap(), applicationContext);
//            html= thymeleafViewResolver.getTemplateEngine().process("goodsDetail",springWebContext);
            WebContext webContext=new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
            html= thymeleafViewResolver.getTemplateEngine().process("goodsDetail",webContext);
            if (!StringUtils.isEmpty(html)){
                redisService1.set(GoodsKey.getGoodsDetail,""+goodsId,html);//如果不是空  保存到模板中
            }
            return html;
//            return "goodsDetail";
        }
    }

//    没用了
    @RequestMapping(value = "GoodsDetail1/{goodsId}")
    @ResponseBody
    public Result1<DetailRP> detail_goods1(HttpServletRequest request, HttpServletResponse response, Model model, User1 user1, @PathVariable("goodsId") long goodsId) {
        System.out.println("运行到goodscontroller的GoodsDetail/{goodsId} 了");
      {  //如果为空  进行手动渲染

            GoodsRP goods = goodsService.getGoodsRPByGoodsId(goodsId);
            long beginTime = goods.getStartDate().getTime();
            long endTime = goods.getEndDate().getTime();
            long now = System.currentTimeMillis();
            int secKillStatus = 0;  //未开始-1 开始0 结束1
            int remainTime = 0;   //开始倒计时
            if (now < beginTime) {
                //未开始 开始倒计时
                secKillStatus = -1;
                remainTime = (int) ((beginTime - now) / 1000);
            } else if (now > endTime) {
                //秒杀结束
                secKillStatus = 1;
                remainTime = -1;

            } else {
                // 秒杀中
                secKillStatus = 0;
                remainTime = 0;
            }

          DetailRP detailRP=new DetailRP();
          detailRP.setGoodsRP(goods);
          detailRP.setUser(user1);
          detailRP.setSecKillStatus(secKillStatus);
          detailRP.setRemainTime(remainTime);
          return Result1.suc(detailRP);
//            return "goodsDetail";
        }
    }
    @RequestMapping(value = "GoodsDetail/{goodsId}")
    @ResponseBody
    public Result1<SecKillDetailRP> detail_goods(HttpServletRequest request, HttpServletResponse response, Model model, User user, @PathVariable("goodsId") long goodsId) {
        System.out.println(">>>运行到goodscontroller的GoodsDetail/{goodsId} 了");
        GoodsRP goods = goodsService.getGoodsRPByGoodsId(goodsId);
        SecKillGoodsRP secKillGoodsRP=goodsService.getSecKillGoodsRPByGoodsId(goodsId);
        System.out.println("视图为"+secKillGoodsRP);
//        long beginTime = secKillGoodsRP.getStartDate().getTime();
//        long endTime = secKillGoodsRP.getEndDate().getTime();
//        long now = System.currentTimeMillis();
//        int secKillStatus = 0;  //未开始-1 开始0 结束1
//        int remainTime = 0;   //开始倒计时
//        if (now < beginTime) {
//            //未开始 开始倒计时
//            secKillStatus = -1;
//            remainTime = (int) ((beginTime - now) / 1000);
//        } else if (now > endTime) {
//            //秒杀结束
//            secKillStatus = 1;
//            remainTime = -1;
//
//        } else {
//            // 秒杀中
//            secKillStatus = 0;
//            remainTime = 0;
//        }

        SecKillDetailRP secKillDetailRP=new SecKillDetailRP();
        secKillDetailRP.setSecKillGoodsRP(secKillGoodsRP);
        secKillDetailRP.setUser(user);
        secKillDetailRP.setRemainTime(0);
        secKillDetailRP.getSecKillGoodsRP().setIsSecKill(1);
        return Result1.suc(secKillDetailRP);

    }
}

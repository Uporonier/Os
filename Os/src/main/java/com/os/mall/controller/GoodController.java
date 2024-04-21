package com.os.mall.controller;

import com.os.mall.service.GoodService;
import com.os.mall.SecKill.controller.GoodsController;
import com.os.mall.annotation.Authority;
import com.os.mall.constants.Constants;
import com.os.mall.common.Result;
import com.os.mall.entity.AuthorityType;
import com.os.mall.entity.Good;
import com.os.mall.entity.Standard;
import com.os.mall.entity.dto.GoodDTO;
import com.os.mall.service.StandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

//对应商品规格表
// goods_id  value   price   store
//   5,        白色, 145.00,  299

@RestController
@RequestMapping("/api/good")
public class GoodController {
    @Resource
    private GoodService goodService;

    @Resource
    private StandardService standardService;



    @Authority(AuthorityType.requireAuthority)
    @PostMapping
    public Result save(@RequestBody Good good) {
        System.out.println(good);
        return Result.success(goodService.saveOrUpdateGood(good));
    }

    @Authority(AuthorityType.requireAuthority)
    @PutMapping
    public Result update(@RequestBody Good good) {
        goodService.update(good);
        return Result.success();
    }

    @Authority(AuthorityType.requireAuthority)
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        goodService.deleteGood(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        return Result.success(goodService.getGoodById(id));
    }

    //获取商品的规格信息
    @GetMapping("/standard/{id}")
    public Result getStandard(@PathVariable int id) {
        return Result.success(goodService.getStandard(id));
    }
    //查询推荐商品，即recommend=1
    @GetMapping
    public Result findAll() {

        return Result.success(goodService.findFrontGoods());
    }
    //查询销量排行
    @GetMapping("/rank")
    public Result getSaleRank(@RequestParam int num){
        return Result.success(goodService.getSaleRank(num));
    }
    //保存商品的规格信息
    @PostMapping("/standard")
    public Result saveStandard(@RequestBody List<Standard> standards, @RequestParam int goodId) {
        //先删除全部旧记录
        standardService.deleteAll(goodId);
        //然后插入新记录
        for (Standard standard : standards) {
            standard.setGoodId(goodId);
            if(!standardService.save(standard)){
                return Result.error(Constants.CODE_500,"保存失败");
            }
        }
        return Result.success();
    }

    //删除商品的规格信息
    @Authority(AuthorityType.requireAuthority)
    @DeleteMapping("/standard")
    public Result delStandard(@RequestBody Standard standard) {
        boolean delete = standardService.delete(standard);
        if(delete) {
            return Result.success();
        }else {
            return Result.error(Constants.CODE_500,"删除失败");
        }
    }

    //修改商品的推荐字段
    @Authority(AuthorityType.requireAuthority)
    @GetMapping("/recommend")
    public Result setRecommend(@RequestParam Long id,@RequestParam Boolean isRecommend){
        return Result.success(goodService.setRecommend(id,isRecommend));
    }

    @GetMapping("/page")
    public Result findPage(
                            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                            @RequestParam(required = false, defaultValue = "") String searchText,
                            @RequestParam(required = false) Integer categoryId) {

        return Result.success(goodService.findPage(pageNum,pageSize,searchText,categoryId));
    }



    @GetMapping("/fullPage")
    public Result findFullPage(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(required = false) Integer categoryId) {

        return Result.success(goodService.findFullPage(pageNum,pageSize,searchText,categoryId));
    }

    @Autowired
    GoodsController goodsController;
    // 修改商品的秒杀状态
    @PostMapping("/setseckill")
    public Result setSecKill(@RequestBody GoodDTO goodDTO,@RequestParam(value = "isSecKill")int isSecKill,
                             @RequestParam(value = "seckillPrice") float seckillPrice,
                             @RequestParam(value = "stock") int stock) {
        try {
            // 根据商品ID查询商品信息
            Good good = goodService.getGoodById(goodDTO.getId());
            if (good == null) {
                return Result.error("404", "商品不存在");
            }
            // 设置商品为秒杀商品
            good.setIsSecKill(isSecKill);
            good.setRecommend(false);   //
            // 更新商品信息
            goodService.updateGood(good);
            goodsController.addSecGoods(goodDTO.getId(),seckillPrice,stock);


            return Result.success("修改是否为秒杀商品成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("500", "设置秒杀商品失败");
        }
    }
    @PostMapping("/setnotseckill")
    public Result setNotSecKill(@RequestBody GoodDTO goodDTO,@RequestParam(value = "isSecKill")int isSecKill) {
        //  1. 商品表 isSecKill设置为1
        // 根据商品ID查询商品信息
        Good good = goodService.getGoodById(goodDTO.getId());
        if (good == null) {
            return Result.error("404", "商品不存在");
        }
        // 设置商品为秒杀商品
        good.setIsSecKill(isSecKill);
        // 更新商品信息
        goodService.updateGood(good);

        //2. 删除秒杀商品表中对应的信息
                goodsController.delSecGoods(goodDTO.getId());
                return Result.success("删除了秒杀商品");
            }

}

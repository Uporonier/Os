package com.os.mall.controller;

import com.os.mall.service.*;
import com.os.mall.common.Result;
import com.os.mall.entity.Good;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recommend")
public class RecommendController {


    @Autowired
    private ResNetService resNetService;
    @Autowired
    private UserEventsService userEventsService;

    @Autowired
    private RecommendationService recommendationService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodService goodService; // 引入GoodService以获取商品详细信息


    @GetMapping("/user/{userId}")
    public Result recommendForUser(@PathVariable Long userId) {
        // 假定每次推荐5个商品
        int topN = 5;

        // 协同过滤算法获得的
        List<Long> recommendations = orderService.recommendProductsBasedOnPurchaseHistory(userId);

    // ResNet图像检索获得的
        List<Long> goods = userEventsService.getAllProductIdsByUserId(userId);
        Set<Integer> recommend_Goodsid = resNetService.getRecommendations(goods);

    // 创建一个Set来合并推荐并自动去重
        Set<Long> combinedRecommendations = new HashSet<>(recommendations);
    // 将Integer转换为Long并添加到Set中
        for (Integer id : recommend_Goodsid) {
            combinedRecommendations.add(id.longValue());
        }

        System.out.println("总的为"+combinedRecommendations);
        // 使用GoodService根据商品ID列表获取完整的商品信息
        List<Good> recommendedProducts =   combinedRecommendations.stream()
                .map(goodId -> {
                    try {
                        return goodService.getGoodById(goodId);
                    } catch (Exception e) {
                        System.err.println("Error retrieving product details for ID: " + goodId + " - " + e.getMessage());
                        return null;
                    }
                })
                .filter(good -> good != null) // 确保不添加 null 到结果列表
                .collect(Collectors.toList());
        return Result.success(recommendedProducts);
    }

    @GetMapping("/page/{userId}")
    public Result recommendForUser(@PathVariable Long userId,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "8") int size) {
//??
        // 分页参数
        PageRequest pageRequest = PageRequest.of(page, size);

        // 调用服务层方法，获取分页推荐商品
        Page<Good> recommendedProductsPage = orderService.recommendProductsBasedOnPurchaseHistory1(userId, pageRequest);

        // 返回分页结果
        return Result.success(recommendedProductsPage);
    }
}

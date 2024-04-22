package com.os.mall.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.os.mall.SecKill.redis.OrderKey;
import com.os.mall.SecKill.redis.RedisService1;
import com.os.mall.constants.Constants;
import com.os.mall.entity.Good;
import com.os.mall.entity.Order;
import com.os.mall.entity.OrderGoods;
import com.os.mall.entity.OrderItem;
import com.os.mall.entity.*;
import com.os.mall.exception.ServiceException;
import com.os.mall.mapper.GoodMapper;
import com.os.mall.mapper.OrderGoodsMapper;
import com.os.mall.mapper.OrderMapper;
import com.os.mall.mapper.StandardMapper;
import com.os.mall.utils.TokenUtils;
import javafx.beans.binding.Bindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.os.mall.constants.RedisConstants.GOOD_TOKEN_KEY;

@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderGoodsMapper orderGoodsMapper;
    @Resource
    private StandardMapper standardMapper;
    @Resource
    private GoodMapper goodMapper;
    @Resource
    private CartService cartService;
    @Resource
    private RedisTemplate<String, Good> redisTemplate;


    @Transactional
    public String saveOrder(Order order,Long goodsid) {
        order.setUserId(TokenUtils.getCurrentUser().getId());
        String orderNo = DateUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(6);
        order.setOrderNo(orderNo);
        order.setGoodsid(goodsid);
        order.setCreateTime(DateUtil.now());
        orderMapper.insert(order);

        OrderGoods orderGoods = new OrderGoods();
        orderGoods.setOrderId(order.getId());
        //遍历order里携带的goods数组，并用orderItem对象来接收
        String goods = order.getGoods();
        List<OrderItem> orderItems = JSON.parseArray(goods, OrderItem.class);
        for (OrderItem orderItem : orderItems) {
            long good_id = orderItem.getId();
            String standard = orderItem.getStandard();
            int num = orderItem.getNum();
            orderGoods.setGoodId(good_id);
            orderGoods.setCount(num);
            orderGoods.setStandard(standard);
            //插入到order_good表
            orderGoodsMapper.insert(orderGoods);
        }
        // 清除购物车
        cartService.removeById(order.getCartId());
        return orderNo;
    }

    //给订单付款
    @Transactional
    public void payOrder(String orderNo) {
        System.out.println("调用了orderservice的payOrder");
        //更改状态为待付款
        orderMapper.payOrder(orderNo);
        //给对应规格减库存
        Map<String, Object> orderMap = orderMapper.selectByOrderNo(orderNo);
        int count = (int) orderMap.get("count");
        Object goodIdObj = orderMap.get("goodId");
        Long goodId = null;
        if(goodIdObj instanceof Long) {
            goodId = (Long) goodIdObj;
        } else if(goodIdObj != null) {
            try {
                goodId = Long.parseLong(goodIdObj.toString());
            } catch (NumberFormatException e) {
                throw new ServiceException(Constants.CODE_500, "商品ID不正确");
            }
        }

        if(goodId == null) {
            throw new ServiceException(Constants.CODE_500, "商品ID不存在");
        }
        String standard = (String) orderMap.get("standard");
        int store = standardMapper.getStore(goodId, standard);
        if (store < count) {
            throw new ServiceException(Constants.CODE_500, "库存不足");
        }
        standardMapper.deductStore(goodId, standard, store - count);

        //给对应商品加销量和销售额
        LambdaQueryWrapper<Order> orderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderLambdaQueryWrapper.eq(Order::getOrderNo, orderNo);
        Order one = getOne(orderLambdaQueryWrapper);
        BigDecimal totalPrice = one.getTotalPrice();
        goodMapper.saleGood(goodId, count, totalPrice);

        // redis 增销量
        String redisKey = GOOD_TOKEN_KEY + goodId;
        ValueOperations<String, Good> valueOperations = redisTemplate.opsForValue();
        Good good = valueOperations.get(redisKey);
        if(!ObjectUtils.isEmpty(good)) {
            good.setSales(good.getSales() + count);
            valueOperations.set(redisKey, good);
        }
    }

    public List<Map<String, Object>> selectByUserId(int userId) {
        return orderMapper.selectByUserId(userId);
    }

    public boolean receiveOrder(String orderNo) {
        return orderMapper.receiveOrder(orderNo);
    }

    public Map<String, Object> selectByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }
    public Map<String, Object> selectByOrderNo1(String orderNo) {
        return orderMapper.selectByOrderNo1(orderNo);
    }
    public void delivery(String orderNo) {
        LambdaUpdateWrapper<Order> orderLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        orderLambdaUpdateWrapper.eq(Order::getOrderNo, orderNo)
                .set(Order::getState, "已发货");
        update(orderLambdaUpdateWrapper);
    }

    public Order getByOrderNo(String orderId) {
        return orderMapper.getByOrderNo(orderId);
    }

    @Autowired
    RedisService1 redisService;
    public Order getSecKillOrderByUserIdGoodsId(long userId, long goodsId) {
        return redisService.get(OrderKey.getSecKillOrderByUserIdwithGoodsid,
                "-"+userId+"-"+goodsId, Order.class);
    }

    List<Order> findAllOrders() {
        return orderMapper.findAllOrders();
    }


    /****************   协同过滤算法推荐商品  *******************/
    public List<Long> recommendProductsBasedOnPurchaseHistory(Long userId) {
        // 步骤1: 构建用户到商品的映射
        Map<Long, Set<Long>> userToProductsMap = getUserToProductsMap();
//        System.out.println("userToProductsMap"+userToProductsMap.toString());
        // 当前用户的购买商品集合
        Set<Long> currentUserProducts = userToProductsMap.getOrDefault(userId, Collections.emptySet());
        System.out.println("currentUserProducts"+currentUserProducts.toString());

        // 步骤2: 计算用户间的相似度
        Map<Long, Double> userSimilarityScores = new HashMap<>();
        for (Map.Entry<Long, Set<Long>> entry : userToProductsMap.entrySet()) {
            if (!entry.getKey().equals(userId)) {
                Set<Long> otherUserProducts = entry.getValue();
                // 计算Jaccard相似度
                double similarityScore = calculateJaccardSimilarity(currentUserProducts, otherUserProducts);
                userSimilarityScores.put(entry.getKey(), similarityScore);
//                System.out.println(entry.getKey()+"   "+similarityScore);
            }
        }

        // 步骤3: 为目标用户找到最相似的用户
        List<Long> mostSimilarUserIds = userSimilarityScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .limit(1) // 假设我们取相似度最高的100个用户
                .collect(Collectors.toList());

        // 步骤4: 生成商品推荐

        Set<Long> recommendedProducts = new HashSet<>();

        //先加上自己买过的商品
        recommendedProducts.addAll(currentUserProducts);
//        System.out.println("目前包含的");
//        System.out.println(recommendedProducts);
        for (Long similarUserId : mostSimilarUserIds) {
            Set<Long> similarUserProducts = userToProductsMap.get(similarUserId);
            for (Long productId : similarUserProducts) {
                    recommendedProducts.add(productId);
            }
        }



        return new ArrayList<>(recommendedProducts);
    }

    private double calculateJaccardSimilarity(Set<Long> set1, Set<Long> set2) {
        Set<Long> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        Set<Long> union = new HashSet<>(set1);
        union.addAll(set2);
        if(union.isEmpty())
            return 0;
        return (double) intersection.size() / union.size();
    }

    private Map<Long, Set<Long>> getUserToProductsMap() {
        List<Order> orders = orderMapper.findAllOrders(); // 使用OrderMapper获取所有订单
        Map<Long, Set<Long>> userToProductsMap = new HashMap<>();

        for (Order order : orders) {
            // 假设每个订单只对应一个商品，这里的逻辑根据实际数据结构进行调整
            userToProductsMap.computeIfAbsent(Long.parseLong(String.valueOf(order.getUserId())), k -> new HashSet<>()).add(order.getGoodsid());
        }

        return userToProductsMap;
    }
    @Autowired
    OrderService orderService;
    @Autowired
    GoodService goodService;
    public Page<Good> recommendProductsBasedOnPurchaseHistory1(Long userId, PageRequest pageRequest) {
        // 获取推荐商品ID列表
        List<Long> recommendations =  orderService.recommendProductsBasedOnPurchaseHistory(userId);;// 实现获取推荐商品ID的逻辑

                // 根据推荐ID获取商品详细信息，并支持分页
                // 这里假设goodService有方法支持根据ID列表分页查询商品
        List<Good> recommendedGoods = recommendations.stream()
                        .map(goodId -> goodService.getGoodById(goodId))
                        .collect(Collectors.toList());
        System.out.println(recommendedGoods);

        // 这里是模拟分页逻辑，你需要替换为实际的分页逻辑
        // 假设从数据库或其他服务获取了全部推荐商品后进行内存分页
//        int start = (int) pageRequest.getOffset();
//        int end = (start + pageRequest.getPageSize()) > recommendedGoods.size() ? recommendedGoods.size() : (start + pageRequest.getPageSize());
//        Page<Good> goodsPage = new PageImpl<>(recommendedGoods.subList(start, end), pageRequest, recommendedGoods.size());
// 确保start不会超过列表的最大索引
        int start = Math.min((int) pageRequest.getOffset(), recommendedGoods.size());
// 保持原有的end索引计算逻辑
        int end = Math.min(start + pageRequest.getPageSize(), recommendedGoods.size());
// 使用安全的start和end索引值创建子列表
        Page<Good> goodsPage = new PageImpl<>(recommendedGoods.subList(start, end), pageRequest, recommendedGoods.size());

        return goodsPage;
    }
    public Map<String, Integer> calculateSalesByHour() {
        List<Order> orders = this.list(); // 假设获取所有订单
        Map<String, Integer> salesByHour = new HashMap<>();

        orders.forEach(order -> {
            String hour = "";
            // 创建格式器
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH");

            // 使用格式器格式化当前时间
            // 获取当前的时间
            LocalTime now = LocalTime.now();
            hour = now.format(formatter);
            salesByHour.merge(hour, 1, Integer::sum);
        });

        return salesByHour;
    }
}

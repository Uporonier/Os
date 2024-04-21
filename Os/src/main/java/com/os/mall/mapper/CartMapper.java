package com.os.mall.mapper;

import com.os.mall.entity.Cart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

public interface CartMapper extends BaseMapper<Cart> {

    @MapKey("id")
    List<Map<String, Object>> selectByUserId(Long userId);
    List<Map<String, Object>> selectSecKillCartByUserId(Long userId);

    Cart selectByByUserIdGoodsId(Integer userId, long goodsId);
}

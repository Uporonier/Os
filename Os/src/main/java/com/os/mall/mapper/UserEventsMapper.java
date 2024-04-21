package com.os.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.os.mall.entity.Order;
import com.os.mall.entity.UserEvents;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface UserEventsMapper extends BaseMapper<UserEvents> {

    void insertUserEvent(UserEvents event);
    int countUserEvent(Integer userId, Long goodsId);

    List<Long> findAllProductIdsByUserId(@Param("userId") Long userId);
}

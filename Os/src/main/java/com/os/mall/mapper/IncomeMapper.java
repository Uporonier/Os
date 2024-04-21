package com.os.mall.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IncomeMapper {
    @MapKey("categoryId")
    List<Map<String, Object>> selectCategoryIncome();

    BigDecimal selectSumIncome();

    BigDecimal getDayIncome(@Param("thisDay")String thisDay,@Param("nextDay") String nextDay);

    // 在 IncomeMapper.java 接口中添加

    List<Map<String, Object>> selectHourlyOrders(@Param("date") String date);
}

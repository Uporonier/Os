package com.os.mall.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.os.mall.mapper.IncomeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class IncomeService {
    @Resource
    private IncomeMapper incomeMapper;

    public Map<String,Object> getChart() {
        Map<String, Object> chartMap = new HashMap<>();
        //查询每个分类及其收入
        List<Map<String, Object>> categoryIncomes = incomeMapper.selectCategoryIncome();
        //查询总收入
        BigDecimal sumIncome = incomeMapper.selectSumIncome();
        //放入HashMap中并返回
        chartMap.put("categoryIncomes",categoryIncomes);
        chartMap.put("sumIncome",sumIncome);
        return chartMap;
    }

    public Map<String,Object> getWeekIncome() {
        ArrayList<BigDecimal> weekIncome = new ArrayList<>();
        ArrayList<String> weekDays = new ArrayList<>();
        DateTime dateTime = DateUtil.beginOfWeek(DateUtil.date());
        for (int i = 0; i < 7; i++) {
            DateTime thisDay = DateUtil.offsetDay(dateTime, i);
            DateTime nextDay = DateUtil.offsetDay(dateTime, i+1);
            String weekDay = thisDay.toString();
            String formattedWeekday = weekDay.substring(weekDay.indexOf("-")+1,weekDay.indexOf(" "));
            weekDays.add(formattedWeekday);
            BigDecimal dayIncome = incomeMapper.getDayIncome(thisDay.toString(), nextDay.toString());
            if(dayIncome == null){
                dayIncome = new BigDecimal(0);
            }
            weekIncome.add(dayIncome);
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("weekDays",weekDays);
        map.put("weekIncome",weekIncome);
        return map;
    }

    public Map<String,Object> getMonthIncome() {
        ArrayList<BigDecimal> monthIncome = new ArrayList<>();
        ArrayList<String> monthDays = new ArrayList<>();
        DateTime dateTime = DateUtil.beginOfMonth(DateUtil.date());
        for (int i = 0; i < 30; i++) {
            DateTime thisDay = DateUtil.offsetDay(dateTime, i);
            DateTime nextDay = DateUtil.offsetDay(dateTime, i+1);
            String weekDay = thisDay.toString();
            String formattedWeekday = weekDay.substring(weekDay.indexOf("-")+1,weekDay.indexOf(" "));
            monthDays.add(formattedWeekday);
            BigDecimal dayIncome = incomeMapper.getDayIncome(thisDay.toString(), nextDay.toString());
            if(dayIncome == null){
                dayIncome = new BigDecimal(0);
            }
            monthIncome.add(dayIncome);
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("monthDays",monthDays);
        map.put("monthIncome",monthIncome);
        return map;
    }

    public List<Integer> getDailyTransactions(String date) {
        System.out.println(date);
        List<Map<String, Object>> hourlyOrdersData = incomeMapper.selectHourlyOrders(date);
        Map<Integer, Integer> hourlyCounts = new HashMap<>();

        // 填充查询结果到映射中
        for (Map<String, Object> record : hourlyOrdersData) {
            Integer hour = (Integer) record.get("hour");
            Integer count = ((Long) record.get("count")).intValue(); // 确保转换正确
            hourlyCounts.put(hour, count);
        }

        // 确保返回一个包含24小时数据的列表
        List<Integer> completeHourlyOrders = new ArrayList<>(Collections.nCopies(24, 0));
        hourlyCounts.forEach((hour, count) -> {
            completeHourlyOrders.set(hour, count);
        });

        return completeHourlyOrders;
    }


}

package com.os.mall.controller;



import com.os.mall.common.Result;
import com.os.mall.entity.AuthorityType;
import com.os.mall.service.IncomeService;
import com.os.mall.annotation.Authority;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Authority(AuthorityType.requireAuthority)
@RestController
@RequestMapping("/api/income")
public class IncomeController {

    @Resource
    private IncomeService incomeService;

    @GetMapping("/chart")
    public Result getChart(){
        return Result.success(incomeService.getChart());
    }
    @GetMapping("/week")
    public Result getWeekIncome(){
        return Result.success(incomeService.getWeekIncome());
    }

    @GetMapping("/month")
    public Result getMonthIncome(){
        return Result.success(incomeService.getMonthIncome());
    }

    @GetMapping("/day")
    public Result getDailyTransactions(@RequestParam String date) {
        try {
            // 转换日期格式，假设前端传入的日期格式是 "yyyy-MM-dd"
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = formatter.parse(date);

            // 将日期转换回适合数据库查询的格式
            String formattedDate = formatter.format(parsedDate);
            System.out.println("----------------------");
            System.out.println(parsedDate);
            // 获取数据
            List<Integer> hourlyOrders = incomeService.getDailyTransactions(formattedDate);
            System.out.println(hourlyOrders);

            // 构建响应
            return Result.success(hourlyOrders);
        } catch (ParseException e) {
            // 处理日期解析异常
            return Result.error("","Invalid date format. Please use 'yyyy-MM-dd'.");
        } catch (Exception e) {
            // 其他异常处理
            return Result.error("","Failed to retrieve data: " + e.getMessage());
        }
    }

}

package com.os.mall.SecKill.controller;

import com.os.mall.SecKill.Result.Result1;
import com.os.mall.SecKill.rabbitMQ.RabbitMQSender;
import com.os.mall.entity.AuthorityType;
import com.os.mall.annotation.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@CrossOrigin
@Authority(AuthorityType.noRequire)
@RequestMapping("/mqtest")
public class RabbitMQTestController {
    @Autowired
    RabbitMQSender rabbitMQSender;

    @RequestMapping("/mq")
    @ResponseBody
    public Result1<String> mq(){
//        rabbitMQSender.sendMsg("Acer");
        return Result1.suc("123");
    }
}

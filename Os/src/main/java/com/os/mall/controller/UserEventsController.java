package com.os.mall.controller;

import com.os.mall.entity.User;
import com.os.mall.entity.UserEvents;
import com.os.mall.service.UserEventsService;
import com.os.mall.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/userevents")
@ResponseBody
public class UserEventsController {

    @Autowired
    UserEventsService userEventsService;
    @PostMapping("add")
    public void addUserEvents(@RequestParam(value = "goodsid") Long goodsid){
        User user= UserHolder.getUser();
        UserEvents userEvents=new UserEvents();
        userEvents.setGoodsid(goodsid);
        userEvents.setUserid(user.getId());
//        userE
        userEventsService.add(userEvents);
    }
}

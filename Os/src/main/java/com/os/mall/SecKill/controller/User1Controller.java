package com.os.mall.SecKill.controller;

import com.os.mall.SecKill.Result.Result1;
import com.os.mall.SecKill.entity.User1;
import com.os.mall.SecKill.service.User1Service;
import com.os.mall.entity.AuthorityType;
import com.os.mall.annotation.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@CrossOrigin
@Authority(AuthorityType.noRequire)
@RequestMapping("/user1")
public class User1Controller {
    @Autowired
    User1Service user1Service;
    @RequestMapping("/getuser")
    @ResponseBody
    public Result1<User1> getuser(){
        long tmp=1;
        User1 user1 = user1Service.getById(tmp);
        return Result1.suc(user1);
    }
    @RequestMapping("/getinfo")
    @ResponseBody
    public Result1<User1> GetInfo(Model model, User1 user1){
        return Result1.suc(user1);
    }





}

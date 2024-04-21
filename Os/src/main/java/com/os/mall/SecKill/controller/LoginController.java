package com.os.mall.SecKill.controller;

import com.os.mall.SecKill.redis.RedisService1;
import com.os.mall.SecKill.service.User1Service;
import com.os.mall.SecKill.Result.CodeMessage;
import com.os.mall.SecKill.Result.Result1;
import com.os.mall.SecKill.entity.User1;
import com.os.mall.SecKill.recParam.LoginRP;
import com.os.mall.annotation.Authority;
import com.os.mall.entity.AuthorityType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller

@CrossOrigin
@Authority(AuthorityType.noRequire)
@RequestMapping("/login1")
public class LoginController {

    @Autowired
    User1Service user1Service;
    @Autowired
    RedisService1 redisService1;
    private static Logger logger= LoggerFactory.getLogger(LoginController.class);

    @Authority(AuthorityType.noRequire)
    @RequestMapping("/toLogin1")
//    @ResponseBody
    public String toLogin(){

        System.out.println("tologin!");
        return "login";
    }

    @Authority(AuthorityType.noRequire)
    @RequestMapping("/doLogin")
    @ResponseBody
    public Result1<String> doLogin(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, LoginRP loginRP){
        System.out.println(loginRP);
        logger.info(loginRP.toString());

        //验证手机号合法性
//        String mobile= loginRP.getMobile();
//        if(!MobileVerify.isMobile(mobile)){
//            return Result1.error(CodeMessage.LoginMobile_ERROR);
//        }
        //验证密码是否为空
        String passwordInput= loginRP.getPassword();
        if(StringUtils.isEmpty(passwordInput)){
            return Result1.error(CodeMessage.LoginPass_ERROR);
        }
        CodeMessage codeMessage=(user1Service.login(httpServletResponse,httpServletRequest, loginRP));

        String token = codeMessage.token_tmp;
        if(codeMessage.getCode()==0)
        {
            System.out.println("token====="+token);

            User1 user1 = user1Service.getByToken(httpServletResponse,httpServletRequest,token);
//            return  Result1.suc(user1.getRole());
            return Result1.suc(token);
        }else
            return Result1.error(codeMessage);

//        return  Result.suc(true);
    }
}

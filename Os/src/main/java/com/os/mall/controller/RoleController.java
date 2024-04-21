package com.os.mall.controller;

import com.os.mall.common.Result;
import com.os.mall.entity.User;
import com.os.mall.utils.TokenUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleController {
    @PostMapping("/role")
    public Result getUserRole(){    //redis读出来当前token的对象  然后返回对象的role
        User currentUser = TokenUtils.getCurrentUser();
        return Result.success(currentUser.getRole());
    }
}

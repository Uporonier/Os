package com.os.mall.SecKill.config;

import com.os.mall.SecKill.entity.User1;
import com.os.mall.SecKill.service.User1Service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@ComponentScan(basePackages = "com.rabbiter.em.SecKill")
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    User1Service user1Service;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> cla=methodParameter.getParameterType();//获取参数的类型
        return cla== User1.class;//判断是不是User类型
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest httpServletRequest =  nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse httpServletResponse =  nativeWebRequest.getNativeResponse(HttpServletResponse.class);

        String paramToken =httpServletRequest.getParameter(User1Service.COOKIE_NAME_TOKEN);
        String cookieToken=getCookieName(httpServletRequest, User1Service.COOKIE_NAME_TOKEN);
        if(StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken)){
            return null;
        }
        String token=StringUtils.isEmpty(paramToken)?cookieToken:paramToken; //优先取param
//        String token=StringUtils.isEmpty(paramToken)?paramToken:cookieToken; //优先取param
        return user1Service.getByToken(httpServletResponse,httpServletRequest,token);
    }

    private String getCookieName(HttpServletRequest httpServletRequest, String cookieNameToken) {
        Cookie [] cookies=httpServletRequest.getCookies();
        if(cookies == null ||cookies.length==0)  {
            System.out.println("null");
            return null;
        }
        System.out.println("执行到这了");
        for (Cookie cookie:cookies){
            if (cookie.getName().equals(cookieNameToken))
                return cookie.getValue();
        }
        return null;
    }
}

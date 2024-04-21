package com.os.mall.config;

import com.os.mall.interceptor.AuthorityInterceptor;
import com.os.mall.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Resource
    JwtInterceptor jwtInterceptor;
    @Resource
    AuthorityInterceptor authorityInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //jwt拦截器
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login","/register","/file/**","/avatar/**","/api/good/**","/api/icon/**","/api/category/**","/api/market/**","/api/carousel/**"
                ,"/login1","/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs","/alipay/notify","/alipay/pay"
                )
                .order(0)
        ;
        //权限校验拦截器
        registry.addInterceptor(authorityInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(  "/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs")
                .order(1)
        ;

        WebMvcConfigurer.super.addInterceptors(registry);
    }


}

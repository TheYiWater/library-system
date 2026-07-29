package com.example.librarysystem.config;

import com.example.librarysystem.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")           // 拦截所有 /api 开头的接口
                .excludePathPatterns(                  // 放行这些接口，不用登录
                        "/api/users/login",
                        "/api/users/register",
                        "/api/test",
                        "/api/books/**",                // 查图书接口不用登录，让谁都能看
                        "/api/categories/**"            // 查分类接口不用登录
                );
    }
}
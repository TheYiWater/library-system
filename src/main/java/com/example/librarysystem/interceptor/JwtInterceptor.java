package com.example.librarysystem.interceptor;

import com.example.librarysystem.common.BusinessException;
import com.example.librarysystem.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Collections;
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("请求路径: {}, 所有Header: {}", request.getRequestURI(), Collections.list(request.getHeaderNames()));
        String token = request.getHeader("Authorization");
        log.info("Authorization header值: {}", token);
        if (token == null || token.isEmpty()) {
            throw new BusinessException(401, "请先登录");
        }
        // Token 格式：Bearer xxxxx
        if (!token.startsWith("Bearer ")) {
            throw new BusinessException(401, "Token 格式错误");
        }
        token = token.substring(7);
        try {
            Long userId = JwtUtil.getUserId(token);
            Integer role = JwtUtil.getRole(token);
            // 把 userId 和 role 放到 request 里，后续 Controller/Service 能拿到
            request.setAttribute("userId", userId);
            request.setAttribute("role", role);
            return true;
        } catch (com.auth0.jwt.exceptions.TokenExpiredException e) {
            throw new BusinessException(401, "登录已过期，请重新登录");
        } catch (Exception e) {
            log.error("Token 解析失败: {}", e.getMessage(), e);
            throw new BusinessException(401, "Token 无效");
        }
    }
}
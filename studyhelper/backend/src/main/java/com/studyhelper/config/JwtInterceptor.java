package com.studyhelper.config;

import com.studyhelper.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    private final JwtUtils jwtUtils;
    
    public JwtInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        String authHeader = request.getHeader(jwtUtils.getHeader());
        
        if (authHeader == null || !authHeader.startsWith(jwtUtils.getPrefix() + " ")) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":2001,\"message\":\"未登录\",\"timestamp\":" + System.currentTimeMillis() + "}");
            return false;
        }
        
        String token = authHeader.substring(jwtUtils.getPrefix().length() + 1);
        
        if (!jwtUtils.validateToken(token)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":2002,\"message\":\"无效Token\",\"timestamp\":" + System.currentTimeMillis() + "}");
            return false;
        }
        
        if (jwtUtils.isTokenExpired(token)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":2001,\"message\":\"登录已过期\",\"timestamp\":" + System.currentTimeMillis() + "}");
            return false;
        }
        
        // 将用户信息存入请求属性
        request.setAttribute("userId", jwtUtils.getUserIdFromToken(token));
        request.setAttribute("username", jwtUtils.getUsernameFromToken(token));
        request.setAttribute("role", jwtUtils.getRoleFromToken(token));
        
        return true;
    }
}

package com.example.campus.util;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.info("处理请求: {} method: {}", requestURI, request.getMethod());

        // 对于登录接口不进行JWT验证
        if (requestURI.equals("/login") || requestURI.equals("/api/auth/login")) {
            log.debug("跳过JWT验证: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = getTokenFromRequest(request);
            log.info("从请求头获取token: {}", token != null ? "找到" : "未找到");

            if (token != null && jwtUtil.validateToken(token)) {
                log.info("开始JWT验证");

                // 从token中获取用户信息
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);
                List<String> roles = jwtUtil.getRolesFromToken(token);

                log.info("Token解析结果 - 用户ID: {}, 用户名: {}, 角色: {}", userId, username, roles);

                // 创建权限列表
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> {
                            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                            log.info("创建权限: {}", authority.getAuthority());
                            return authority;
                        })
                        .collect(Collectors.toList());

                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                // 将认证信息存入SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("认证信息设置成功，权限列表: {}", authorities);
            } else {
                log.warn("Token不存在或验证失败");
            }
        } catch (Exception e) {
            log.error("JWT认证处理异常", e);
        }

        filterChain.doFilter(request, response);
        log.info("请求处理完成: {}", requestURI);
    }


    private String getTokenFromRequest(HttpServletRequest request) {
        log.info("=== 查找Authorization头 ===");
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        //没用的while
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            log.info("Header: {} = {}", headerName, headerValue);

            // 不区分大小写检查头名称
            if ("Authorization".equalsIgnoreCase(headerName) && headerValue != null) {
                log.info("通过遍历找到Authorization头: {}", headerValue);
                if (headerValue.startsWith("Bearer ")) {
                    String token = headerValue.substring(7);
                    log.info("成功提取token: {}...", token.substring(0, Math.min(20, token.length())));
                    return token;
                } else {
                    log.warn("Authorization头格式不正确，值: {}", headerValue);
                }
            }
        }
        String tokenHeader = request.getHeader("token");
        if(tokenHeader!= null && !tokenHeader.isEmpty())
        {
            log.info("从header获取令牌");
            return tokenHeader;
        }

        log.warn("未找到有效的Authorization头");
        return null;
    }
}

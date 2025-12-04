package com.example.campus.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.campus.util.JwtAuthenticationEntryPoint;
import com.example.campus.util.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        // 公开接口
                        .requestMatchers("/login").permitAll()

                        // 允许所有 OPTIONS 请求（CORS 预检请求），匹配所有路径
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 个人中心接口：仅允许已认证用户访问
                        .requestMatchers(
                                "/profile",          // 个人中心基础接口
                                "/profile/**"        // 个人中心下所有子接口（如 /profile/info、/profile/update 等）
                        ).authenticated()

                        // 学生接口
                        .requestMatchers("/student/**").hasAuthority("student")

                        // 老师接口
                        .requestMatchers("/teacher/**").hasAuthority("teacher")

                        // 管理员接口
                        .requestMatchers("/admin/**").hasAuthority("super_admin")
                        .requestMatchers("/api/admin/**").hasAuthority("super_admin")

                        // 其他所有请求 - 允许所有已认证用户
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // 添加JWT过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

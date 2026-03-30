package org.example.Config;

import org.example.Fileter.JwtAuthenticationTokenFileter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    // 密码加密
//    @Bean
//    public PasswordEncoder passwordEncoder()
//    {
//        return new BCryptPasswordEncoder();
//    }

    @Autowired
    private JwtAuthenticationTokenFileter jwtAuthenticationTokenFileter;


    // 认证管理器
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception
    {
        return configuration.getAuthenticationManager();
    }

    // 安全过滤链配置
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        http
                // 关闭csrf（前后端分离一般关闭）
                .csrf(csrf -> csrf.disable())   // ✅ 关闭CSRF
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )   // ✅ 无状态

                // 配置接口权限.....................................
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/login").permitAll()  // 登录接口放行
                       .anyRequest().authenticated()       // 其他接口需要登录
                )

                // 关闭默认登录页面
                .formLogin(form -> form.disable());
                //.formLogin(form -> form.permitAll()); // 启用默认登录页面

        http.addFilterBefore(jwtAuthenticationTokenFileter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

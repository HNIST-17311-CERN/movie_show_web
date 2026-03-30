package org.example.Fileter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Entity.LoginUser;
import org.example.Tool.JWT_Utils;
import org.example.Tool.RedisCache;
import org.jsoup.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtAuthenticationTokenFileter extends OncePerRequestFilter
{
    @Autowired
    public RedisCache redisCache;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        //获取token
        String token = request.getHeader("token");
        if(token==null)
        {
           //如果没有token就放行
            filterChain.doFilter(request,response);
            return;
        }

        //解析token
        String userid;
        try
        {
            userid = JWT_Utils.JWT_Parse(token);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        //从redis获取信息
        String redisKey = "login:"+userid;
        LoginUser loginUser = redisCache.getCacheObject(redisKey);
        if(loginUser==null)
        {
            throw new RuntimeException("用户未登录");
        }
        //存入SecurityContextHolder

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser , null , loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        //放行
        filterChain.doFilter(request,response);
    }
}

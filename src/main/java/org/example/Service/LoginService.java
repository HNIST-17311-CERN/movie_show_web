package org.example.Service;

import org.example.Entity.LoginUser;
import org.example.Entity.ResonseResult;
import org.example.Entity.User;
import org.example.Tool.JWT_Utils;
import org.example.Tool.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginService
{

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    public RedisCache redisCache;


    public ResonseResult login(User user) throws Exception
    {
        //进行用户认证
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        //如果没通过，给出提示
        if(authentication==null)
        {
            throw new RuntimeException("登录失败");
        }

        //如果通过了，使用userid生成一个jwt
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        User user_new = loginUser.getUser();
        //String id = user_new.getId().toString();
        String jwt = JWT_Utils.JWT_Creat(user_new.getId());


        //把完整的用户消息存入rides，userid作为key

        String redisKey = "login:" + user_new.getId();
        redisCache.setCacheObject(redisKey, loginUser);

        Map<String,String> map = new HashMap<>();
        map.put("token",jwt);


       return new ResonseResult(200,"登陆成功",map);

    }



    public ResonseResult logout () throws Exception
    {
        //获取SecuritycontexHolder中的用户id
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        Long userid = loginUser.getUser().getId();

        //删除redis的值()
        redisCache.deleteObject("login:"+userid);

        return new ResonseResult(200,"注销成功");
    }





}

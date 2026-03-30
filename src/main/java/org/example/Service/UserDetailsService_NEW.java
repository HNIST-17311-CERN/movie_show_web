package org.example.Service;

import org.example.DAO.UserDAO;
import org.example.Entity.LoginUser;
import org.example.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserDetailsService_NEW implements UserDetailsService
{

    @Autowired
    UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        //查询用户信息
        User user = userDAO.Find_By_Name(username);

        if(user == null)
        {
            throw new UsernameNotFoundException("用户不存在");
        }

        //查询对应的权限
        List<String> list = new ArrayList<>(Arrays.asList("test","admin"));
        System.out.println(list);
        //返回 UserDetails

        return new LoginUser(user,list);
    }


}

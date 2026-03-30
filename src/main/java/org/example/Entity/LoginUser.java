package org.example.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class LoginUser implements UserDetails, Serializable
{

    public LoginUser()
    {
    }

    private User user;   // 数据库里的用户


    public List<String> permissions;

    @JsonIgnore
    List<SimpleGrantedAuthority> authorities;

    public LoginUser(User user, List<String> permissions)
    {
        this.user = user;
        this.permissions = permissions;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {

        //把permissions的string权限信息封装成SimpGrantedAuthority对象
//        List<GrantedAuthority> newList = new ArrayList<>();
//        for (String permission : permissions)
//        {
//           SimpleGrantedAuthority authority = new SimpleGrantedAuthority(permission);
//           newList.add(authority);
//        }
        if (permissions==null)
        {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        if (authorities!=null)
        {
            return authorities;
        }
        authorities = permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return authorities;
    }

    @JsonIgnore
    @Override
    public String getPassword()
    {
        return user.getPassword();
    }

    @JsonIgnore
    @Override
    public String getUsername()
    {
        return user.getUsername();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled()
    {
        return true;
    }
}
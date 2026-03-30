package org.example.Tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisCache {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;;

    //存数据
    public void setCacheObject(String key, Object value){
        redisTemplate.opsForValue().set(key,value);
    }

    //存数据并设置过期时间
    public void setCacheObject(String key,Object value,long timeout, TimeUnit unit){
        redisTemplate.opsForValue().set(key,value,timeout,unit);
    }

    //获取数据
    public <T> T getCacheObject(String key){
        return (T) redisTemplate.opsForValue().get(key);
    }

    //删除数据
    public Boolean deleteObject(String key){
        return redisTemplate.delete(key);
    }
}
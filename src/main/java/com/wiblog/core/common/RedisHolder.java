package com.wiblog.core.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RedisHolder {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 释放锁
     */
    private static final String RELEASE_LOCK_LUA_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    /**
     * 限流
     */
    private static final String LIMIT_FLOW_LUA_SCRIPTS = "if redis.call('zcount',KEYS[1],ARGV[1],ARGV[2] ) < tonumber(ARGV[3])  " +
            "then return redis.call('zadd',KEYS[1],ARGV[4],ARGV[5])  " +
            "else return 0  " +
            "end";

    public void contextLoads() {
        String lockKey = "123";
        String UUID = java.util.UUID.randomUUID().toString();
        boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey,UUID,3, TimeUnit.MINUTES);
        if (!success){
            System.out.println("锁已存在");
        }
        // 指定 lua 脚本，并且指定返回值类型
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(RELEASE_LOCK_LUA_SCRIPT,Long.class);
        // 参数一：redisScript，参数二：key列表，参数三：arg（可多个）
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey),UUID);
        System.out.println(result);
    }
}

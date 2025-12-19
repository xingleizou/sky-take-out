package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        log.info("开始创建RedisTemplate对象...");
        RedisTemplate redisTemplate = new RedisTemplate();
        // 设置RedisConnectionFactory
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        log.info("创建RedisTemplate对象成功！");
        //开始设置RedisTemplate的序列化器
        redisTemplate.setKeySerializer(redisTemplate.getStringSerializer());
        return redisTemplate;
    }
}

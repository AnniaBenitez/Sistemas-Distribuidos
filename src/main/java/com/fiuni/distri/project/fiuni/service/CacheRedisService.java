package com.fiuni.distri.project.fiuni.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CacheRedisService<T> {

    @Value("${app.cache.ttl:8}")
    private long defaultTTL;

    @Autowired
    RedisTemplate<String, T> redisTemplate;

    private String parseValueKey(String value, String key) {
        return (value+"::"+key);
    }

    public void setWithTTL(String value, String key, T object, long duration){
        redisTemplate.opsForValue().set(parseValueKey(value, key), object, Duration.ofHours(duration));
    }

    public void setWithTTL(String value, String key, T object, Duration duration){
        redisTemplate.opsForValue().set(parseValueKey(value, key), object, duration);
    }


    public void setWithDefaultTTL(String value, String key, T object){
        redisTemplate.opsForValue().set(parseValueKey(value, key), object, Duration.ofHours(defaultTTL));
    }

    public T getByValueKey(String value, String key) {
        return (T) redisTemplate.opsForValue().get(parseValueKey(value, key));
    }

    public void clearCache() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    public void removeFromCache(String value, String key) {
        redisTemplate.delete(parseValueKey(value, key));
    }


}
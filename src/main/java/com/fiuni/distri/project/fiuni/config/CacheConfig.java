package com.fiuni.distri.project.fiuni.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;

@Configuration
public class CacheConfig {

    @Value("${app.cache.ttl:10}")
    private long defaultTTL;

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        try {

            //Se inicializa una variable de configuracion de Redis con la configuracion por default
            RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();

            //Se crea un has map para manejar que configuracion sera aplicada para cada key
            HashMap<String, RedisCacheConfiguration> redisCacheConfigurationHashMap = new HashMap<>();

            //Se setean los ttls para los modelos en especifico
            redisCacheConfigurationHashMap.put("beneficio", defaultCacheConfig.entryTtl(Duration.ZERO));
            redisCacheConfigurationHashMap.put("beneficioDetalle", defaultCacheConfig.entryTtl(Duration.ofHours(this.defaultTTL)));
            redisCacheConfigurationHashMap.put("evaluacion", defaultCacheConfig.entryTtl(Duration.ZERO));
            redisCacheConfigurationHashMap.put("evaluacionDetalle", defaultCacheConfig.entryTtl(Duration.ofHours(this.defaultTTL)));

            //Se inicializa redis con las configuraciones
            return RedisCacheManager.builder(redisConnectionFactory).withInitialCacheConfigurations(redisCacheConfigurationHashMap).build();

        } catch (Exception e) {
            // Fallback to a NoOpCacheManager if Redis is not available
            System.out.println("Redis is down, falling back to NoOpCacheManager");
            return new NoOpCacheManager();
        }
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);

        return template;
    }
}

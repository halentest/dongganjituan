package cn.halen.data.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ComponentScan(basePackageClasses = {RedisConfig.class})
public class RedisConfig {
	
	@Bean
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public RedisTemplate redisTemplate() {
        RedisTemplate template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
       // template.setValueSerializer(new Jackson2RedisSerializer());
        return template;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxActive(4);
        jedisPoolConfig.setMaxIdle(2);
        jedisPoolConfig.setMaxWait(60);
        jedisPoolConfig.setTestOnBorrow(true);
        return new JedisConnectionFactory(jedisPoolConfig);
    }
}

package com.tomalancarroll;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootApplication
@EnableCaching
public class SpringBootStarterContentfulApplication {

	@Autowired
	private StringRedisTemplate template;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootStarterContentfulApplication.class, args);
	}

	@Bean
	public CacheManager cacheManager(StringRedisTemplate redisTemplate) {
		return new RedisCacheManager(redisTemplate);
	}
}

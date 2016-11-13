package com.tomalancarroll.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.Locale;

@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... objects) {
                // This will generate the method name
                // and all method parameters appended separated by ':'.
                StringBuilder sb = new StringBuilder();
                sb.append(method.getName().replace("get", ""));
                for (Object obj : objects) {
                    sb.append(':');
                    if (obj instanceof Locale) {
                        sb.append(((Locale) obj).toLanguageTag());
                    } else {
                        sb.append(obj.toString());
                    }
                }
                return sb.toString();
            }
        };
    }
}

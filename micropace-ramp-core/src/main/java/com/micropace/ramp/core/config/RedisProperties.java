package com.micropace.ramp.core.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Redis配置参数注入
 *
 * @author Suffrajet
 */
@Getter
@Setter
@ToString
@Configuration
public class RedisProperties {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.timeout}")
    private int timeout;
    @Value("${spring.redis.pool.min-idle}")
    private int minIdleSize;
    @Value("${spring.redis.pool.max-idle}")
    private int maxIdleSize;
}

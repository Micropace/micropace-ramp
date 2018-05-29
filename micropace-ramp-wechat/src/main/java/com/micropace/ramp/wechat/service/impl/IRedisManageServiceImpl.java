package com.micropace.ramp.wechat.service.impl;

import com.micropace.ramp.wechat.config.RedisProperties;
import com.micropace.ramp.wechat.service.IRedisManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class IRedisManageServiceImpl implements IRedisManageService {

    @Autowired
    private RedisProperties properties;

    @Override
    public JedisPool getJedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMinIdle(properties.getMinIdleSize());
        config.setMaxIdle(properties.getMaxIdleSize());
        config.setMaxTotal(properties.getMaxIdleSize());
        config.setMaxWaitMillis(properties.getTimeout());
        return new JedisPool(
                config,
                properties.getHost(),
                properties.getPort(),
                properties.getTimeout(),
                properties.getPassword());
    }
}

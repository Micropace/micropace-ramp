package com.micropace.ramp.core;

import com.micropace.ramp.base.constant.GlobalConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

/**
 * Redis连接管理器
 *
 * @author Suffrajet
 */
@Component
public class RedisManager {

    /** 统一管理的Redis连接池 */
    @Autowired
    private JedisPool jedisPool;
    /** redis是否可连接 */
    private boolean bRedisConnectActive;
    /** redis操作常用方法类 */
    private RedisHelper redisHelper;

    /**
     * 初始化Redis管理器
     */
    @Autowired
    public void init() {
        this.redisHelper = new RedisHelper(this);
    }

    /**
     * 查询Redis是否可连接
     * @return true: redis正常，可连接; false: redis无法连接
     */
    public boolean isbRedisConnectActive() {
        return bRedisConnectActive;
    }

    /**
     * 检测Redis是否可连接
     */
    public void checkRedisConnectActive() {
        bRedisConnectActive = checkJedisPoolActive();
    }

    /**
     * 获取Redis操作常用方法类
     * @return RedisUtil
     */
    public RedisHelper getHelper() {
        return this.redisHelper;
    }

    /**
     * 从连接池获取连接
     * @return Jedis
     */
    Jedis getJedis() {
        Jedis jedis = null;
        try {
            if(this.jedisPool != null) {
                jedis = this.jedisPool.getResource();
                if(jedis != null && jedis.getClient().isBroken()) {
                    jedis.close();
                    jedis = null;
                }
            }
        } catch (JedisException ignored) {}
        return jedis;
    }

    /**
     * 监测Redis连接池连接是否有效
     * @return boolean
     */
    private boolean checkJedisPoolActive() {
        boolean bActive = false;
        Jedis jedis = null;
        try {
            if(this.jedisPool != null) {
                jedis = jedisPool.getResource();
                if(jedis != null) {
                    String ping = jedis.ping();
                    if (ping.equals(GlobalConst.REDIS_PING)) {
                        bActive = true;
                    }
                }
            }
        } catch (JedisException ignored) {}
        finally {
            if(jedis != null) {
                jedis.close();
            }
        }
        return bActive;
    }
}

package com.micropace.ramp.wechat.core.redis;

import com.micropace.ramp.base.constant.GlobalConst;
import com.micropace.ramp.wechat.service.IRedisManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

/**
 * Redis连接管理器
 *
 * @author Suffrajet
 */
public class RedisManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static RedisManager ourInstance = new RedisManager();

    public static RedisManager getInstance() {
        return ourInstance;
    }

    private RedisManager() {}

    /** 统一管理的Redis连接池 */
    private JedisPool jedisPool;
    /** 是否初始化标识 */
    private boolean bInit = false;
    /** redis管理接口 */
    private IRedisManageService iRedisManageService;
    /** redis是否可连接 */
    private boolean bRedisConnectActive;
    /** redis操作常用方法类 */
    private RedisHelper redisHelper;

    /**
     * 初始化Redis管理器
     * @param iRedisManageService 接口实例
     */
    public void init(IRedisManageService iRedisManageService) {
        this.iRedisManageService = iRedisManageService;
        this.jedisPool = iRedisManageService.getJedisPool();
        this.redisHelper = new RedisHelper();
        this.bInit = true;
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

    public boolean isbInit() {
        return bInit;
    }

    /**
     * 销毁并重建Redis连接池
     */
    public void restart() {
        this.bRedisConnectActive = false;
        try {
            if(this.jedisPool != null) {
                this.jedisPool.destroy();
                this.jedisPool = null;
            }
        } catch (JedisException ignored) {}

        this.jedisPool = iRedisManageService.getJedisPool();
        this.bRedisConnectActive = this.checkJedisPoolActive();
        if(!this.bRedisConnectActive) {
            logger.error("[{}] restart redis pool error, can not connect to redis ", this.getClass().getSimpleName());
        }
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

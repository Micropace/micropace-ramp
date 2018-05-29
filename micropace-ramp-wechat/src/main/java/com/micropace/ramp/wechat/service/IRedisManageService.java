package com.micropace.ramp.wechat.service;

import redis.clients.jedis.JedisPool;

/**
 * Redis连接管理接口
 *
 * @author Suffrajet
 */
public interface IRedisManageService {

    /** 获取redis的连接池对象 */
    JedisPool getJedisPool();
}

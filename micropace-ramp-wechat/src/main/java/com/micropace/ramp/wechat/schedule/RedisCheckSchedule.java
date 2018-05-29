package com.micropace.ramp.wechat.schedule;

import com.micropace.ramp.wechat.core.redis.RedisManager;
import com.micropace.ramp.wechat.service.IRedisManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 全局Redis连接管理器
 *
 * @author Suffrajet
 */
@Service
public class RedisCheckSchedule {

    /** Redis管理接口 */
    @Autowired
    private IRedisManageService iRedisManageService;

    /** 实时监测redis连接是否可用 */
    @Scheduled(
            fixedRate = 1000, initialDelay = 0)
    public void redisConnectionMonitor() {
        if(!RedisManager.getInstance().isbInit()) {
            RedisManager.getInstance().init(iRedisManageService);
        }
        else {
            RedisManager.getInstance().checkRedisConnectActive();
            if (!RedisManager.getInstance().isbRedisConnectActive()) {
                RedisManager.getInstance().restart();
            }
        }
    }
}

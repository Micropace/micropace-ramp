package com.micropace.ramp.wechat.schedule;

import com.micropace.ramp.wechat.core.initializer.GlobalParamManager;
import com.micropace.ramp.wechat.core.redis.RedisManager;
import com.micropace.ramp.base.entity.WxApp;
import com.micropace.ramp.wechat.service.IWxAppService;
import com.micropace.ramp.wechat.service.IWxMsgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GlobalSchedule {
    private static final Logger logger = LoggerFactory.getLogger(GlobalSchedule.class);

    @Autowired
    private IWxMsgService iWxMsgService;
    @Autowired
    private IWxAppService iWxAppService;

    @Scheduled(fixedRate = 1000, initialDelay = 10)
    public void schedule() {
        if(!GlobalParamManager.getInstance().isbInit()) {
            this.init();
        } else {
            if(!RedisManager.getInstance().isbRedisConnectActive()) {
                GlobalParamManager.getInstance().setbInit(false);
            }
        }
    }

    private void init() {
        boolean bInit = false;
        if(RedisManager.getInstance().isbRedisConnectActive()) {
            List<WxApp> wxApps = iWxAppService.selectAll();
            bInit = GlobalParamManager.getInstance().init(wxApps, iWxMsgService);
            GlobalParamManager.getInstance().setbInit(bInit);
        }
        if(bInit) {
            logger.info("Success Init [Schedule {}]", this.getClass().getSimpleName());
        }
    }
}

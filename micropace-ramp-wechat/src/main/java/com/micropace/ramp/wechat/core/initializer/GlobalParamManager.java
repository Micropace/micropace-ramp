package com.micropace.ramp.wechat.core.initializer;

import com.micropace.ramp.base.entity.WxApp;
import com.micropace.ramp.wechat.service.IWxMsgService;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统全局参数初始化管理器
 * 负责为所有公众号创建、本地配置、消息分发服务、消息路由
 *
 * @author Suffrajet
 */
public class GlobalParamManager {

    private static final Logger logger = LoggerFactory.getLogger(GlobalParamManager.class);

    private static GlobalParamManager ourInstance = new GlobalParamManager();

    public static GlobalParamManager getInstance() {
        return ourInstance;
    }

    private GlobalParamManager() {
    }

    /**
     * 微信公众号消息服务管理接口
     */
    private IWxMsgService iWxMsgService;
    /**
     * 多公众号的配置信息，键名为公众号的原始ID
     */
    private volatile Map<String, WxMpConfigStorage> wxAppStorages = new HashMap<>();
    /**
     * 多公众号的消息分发服务，键名为公众号的原始ID
     */
    private volatile Map<String, WxMpService> wxAppServices = new HashMap<>();
    /**
     * 多公众号的消息路由，键名为公众号的原始ID
     */
    private volatile Map<String, WxMpMessageRouter> wxAppRouters = new HashMap<>();

    /**
     * 是否已初始化
     */
    private boolean bInit;

    /**
     * 为已托管的微信公众号创建服务管理接口
     *
     * @param wxApps        已托管的微信公众号
     * @param iWxMsgService 消息服务管理接口
     * @return boolean
     */
    public boolean init(List<WxApp> wxApps, IWxMsgService iWxMsgService) {
        this.iWxMsgService = iWxMsgService;
        if (wxApps != null && wxApps.size() > 0) {
            wxApps.forEach(this::initWxAppService);
        }
        return true;
    }

    /**
     * 动态创建一个微信公众号的服务管理接口
     *
     * @param wxApp 微信公众号信息
     */
    public void addWxApp(WxApp wxApp) {
        this.initWxAppService(wxApp);
    }

    /**
     * 更新一个微信公众号的服务管理接口
     *
     * @param wxApp 微信公众号信息
     */
    public void resetWxApp(WxApp wxApp) {
        this.initWxAppService(wxApp);
    }

    /**
     * 获取微信公众号的本地配置缓存
     *
     * @param wxId 微信公众号信息
     * @return 本地配置缓存
     */
    public WxMpConfigStorage getConfigStorage(String wxId) {
        return this.wxAppStorages.get(wxId);
    }

    /**
     * 获取微信公众号的消息服务接口
     *
     * @param wxId 微信公众号信息
     * @return 消息服务接口
     */
    public WxMpService getService(String wxId) {
        return this.wxAppServices.get(wxId);
    }

    /**
     * 获取微信公众号的消息路由
     *
     * @param wxId 微信公众号信息
     * @return 消息路由
     */
    public WxMpMessageRouter getRouter(String wxId) {
        return this.wxAppRouters.get(wxId);
    }

    public boolean isbInit() {
        return bInit;
    }

    public void setbInit(boolean bInit) {
        this.bInit = bInit;
    }

    private void initWxAppService(WxApp wxApp) {
        WxMpConfigStorage configStorage = this.iWxMsgService.getMsgConfigStorage(wxApp);
        if (configStorage != null) {
            if (this.wxAppStorages.containsKey(wxApp.getWxId())) {
                logger.debug("refresh wxapp {} config storage", wxApp.getWxId());
                this.wxAppStorages.replace(wxApp.getWxId(), configStorage);
            } else {
                logger.debug("init wxapp {} config storage", wxApp.getWxId());
                this.wxAppStorages.put(wxApp.getWxId(), configStorage);
            }
        }
        WxMpService wxMpService = this.iWxMsgService.getMsgService(configStorage);
        if (wxMpService != null) {
            if (this.wxAppServices.containsKey(wxApp.getWxId())) {
                logger.info("refresh wxapp {} message service", wxApp.getWxId());
                this.wxAppServices.replace(wxApp.getWxId(), wxMpService);
            } else {
                logger.info("init wxapp {} message service", wxApp.getWxId());
                this.wxAppServices.put(wxApp.getWxId(), wxMpService);
            }
        }
        WxMpMessageRouter router = this.iWxMsgService.getMsgRouter(wxMpService);
        if (router != null) {
            if (this.wxAppRouters.containsKey(wxApp.getWxId())) {
                logger.debug("refresh wxapp {} message router", wxApp.getWxId());
                this.wxAppRouters.replace(wxApp.getWxId(), router);
            } else {
                logger.debug("init wxapp {} message router", wxApp.getWxId());
                this.wxAppRouters.put(wxApp.getWxId(), router);
            }
        }
    }
}

package com.micropace.ramp.core.dispatch;

import com.micropace.ramp.base.entity.WxApp;
import com.micropace.ramp.base.enums.WxTypeEnum;
import com.micropace.ramp.core.dispatch.handler.*;
import com.micropace.ramp.core.service.IWxAppService;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信公众号消息派发管理器
 * 启动时，负责为所有公众号创建、本地配置、消息分发服务、消息路由
 *
 * @author Suffrajet
 */
@Component
public class MsgDispatchManager {

    private static final Logger logger = LoggerFactory.getLogger(MsgDispatchManager.class);

    @Autowired
    protected LogHandler logHandler;
    @Autowired
    protected NullHandler nullHandler;
    @Autowired
    private MsgLocationHandler msgLocationHandler;
    @Autowired
    private MsgImageHandler msgImageHandler;
    @Autowired
    private MenuHandler menuHandler;
    @Autowired
    private MsgTextHandler msgTextHandler;
    @Autowired
    private MsgScanHandler msgScanHandler;
    @Autowired
    private MsgUnsubscribeHandler msgUnsubscribeHandler;
    @Autowired
    private MsgSubscribeHandler msgSubscribeHandler;

    /** 所有已托管的公众号、小程序 */
    private List<WxApp> wxApps;
    /** 多公众号(订阅号和服务号)的消息分发服务，键名为公众号的原始ID */
    private volatile Map<String, WxMpService> wxMpAppServices = new HashMap<>();
    /** 多公众号(订阅号和服务号)的消息路由，键名为公众号的原始ID */
    private volatile Map<String, WxMpMessageRouter> wxMpAppRouters = new HashMap<>();
    /** 多公众号(企业号)的消息分发服务，键名为公众号的原始ID */
    private volatile Map<String, WxCpService> wxCpAppServices = new HashMap<>();
    /** 多公众号(企业号)的消息路由，键名为公众号的原始ID */
    private volatile Map<String, WxCpMessageRouter> wxCpAppRouters = new HashMap<>();

    // TODO 增加小程序的服务

    /**
     * 为已托管的微信公众号自动创建服务管理接口和消息路由
     */
    @Autowired
    public void init(IWxAppService iWxAppService) {
        List<WxApp> wxApps = iWxAppService.selectAll();
        if (wxApps != null && wxApps.size() > 0) {
            this.wxApps = wxApps;
            wxApps.forEach(this::initWxAppService);
        }
    }

    /**
     * 获取公众号
     *
     * @param wxId 微信公众号原始ID
     * @return WxApp
     */
    public WxApp getWxApp(String wxId) {
        if (this.wxApps == null || this.wxApps.size() == 0) {
            return null;
        }
        for (WxApp e : this.wxApps) {
            if(e.getWxId().equals(wxId)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 获取公众号的类型
     *
     * @param wxId 微信公众号原始ID
     * @return WxTypeEnum
     */
    public WxTypeEnum getWxType(String wxId) {
        if (this.wxApps == null || this.wxApps.size() == 0) {
            return null;
        }

        WxTypeEnum type = null;
        for (WxApp e : this.wxApps) {
            if (e.getWxId().equals(wxId)) {
                type = WxTypeEnum.getByCode(e.getWxType());
            }
        }
        return type;
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
     * 获取订阅号和服务号的消息服务接口
     *
     * @param wxId 微信公众号原始ID
     * @return 消息服务接口
     */
    public WxMpService getMpService(String wxId) {
        return this.wxMpAppServices.get(wxId);
    }

    /**
     * 获取微信企业号的消息服务接口
     *
     * @param wxId 微信公众号原始ID
     * @return 消息服务接口
     */
    public WxCpService getCpService(String wxId) {
        return this.wxCpAppServices.get(wxId);
    }

    /**
     * 获取微信订阅号和服务号的消息路由
     *
     * @param wxId 微信公众号原始ID
     * @return 消息路由
     */
    public WxMpMessageRouter getMpRouter(String wxId) {
        return this.wxMpAppRouters.get(wxId);
    }

    /**
     * 获取微信企业号的消息路由
     *
     * @param wxId 微信公众号原始ID
     * @return 消息路由
     */
    public WxCpMessageRouter getCpRouter(String wxId) {
        return this.wxCpAppRouters.get(wxId);
    }

    /**
     * 根据不同类型公众号来初始化不同的消息处理服务类
     * @param wxApp 微信公众号
     */
    private void initWxAppService(WxApp wxApp) {
        if (WxTypeEnum.APP_SERVICE.getCode().equals(wxApp.getWxType())
                || WxTypeEnum.APP_SUBSCRIBE.getCode().equals(wxApp.getWxType())) {
            this.initMpService(wxApp);
        }
        if (WxTypeEnum.APP_ENTERPRISE.getCode().equals(wxApp.getWxType())) {
            this.initCpService(wxApp);
        }
        if (WxTypeEnum.APP_MINI.getCode().equals(wxApp.getWxType())) {
            this.initMaService(wxApp);
        }
    }

    /** 初始化订阅号和服务号的服务 */
    private void initMpService(WxApp wxApp) {
        if(wxApp == null) {
            return;
        }

        WxTypeEnum wxTypeEnum = WxTypeEnum.getByCode(wxApp.getWxType());
        if(wxTypeEnum == null) {
            logger.error("不支持的微信公众号类型");
            return;
        }

        WxMpInMemoryConfigStorage configStorage = new WxMpInMemoryConfigStorage();
        configStorage.setAppId(wxApp.getAppId());
        configStorage.setSecret(wxApp.getSecret());
        configStorage.setToken(wxApp.getToken());
        configStorage.setAesKey(wxApp.getAesKey());

        WxMpService wxMpService = new me.chanjar.weixin.mp.api.impl.WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(configStorage);
        if (this.wxMpAppServices.containsKey(wxApp.getWxId())) {
            logger.info("更新微信{} {} 消息处理服务", wxTypeEnum.getDesc(), wxApp.getWxId());
            this.wxMpAppServices.replace(wxApp.getWxId(), wxMpService);
        } else {
            logger.info("创建微信{} {} 消息处理服务", wxTypeEnum.getDesc(), wxApp.getWxId());
            this.wxMpAppServices.put(wxApp.getWxId(), wxMpService);
        }

        WxMpMessageRouter router = this.getMpMsgRouter(wxMpService);
        if (router != null) {
            if (this.wxMpAppRouters.containsKey(wxApp.getWxId())) {
                logger.info("更新微信{} {} 消息路由", wxTypeEnum.getDesc(), wxApp.getWxId());
                this.wxMpAppRouters.replace(wxApp.getWxId(), router);
            } else {
                logger.info("创建微信{} {} 消息路由", wxTypeEnum.getDesc(), wxApp.getWxId());
                this.wxMpAppRouters.put(wxApp.getWxId(), router);
            }
        }
    }

    /** 初始化企业号的服务 */
    private void initCpService(WxApp wxApp) {
        WxTypeEnum wxTypeEnum = WxTypeEnum.getByCode(wxApp.getWxType());
        if(wxTypeEnum == null) {
            logger.error("不支持的微信公众号类型");
            return;
        }
        WxCpService wxMpService = this.getCpMsgService(this.getCpMsgConfigStorage(wxApp));
        if (wxMpService != null) {
            if (this.wxCpAppServices.containsKey(wxApp.getWxId())) {
                logger.info("更新微信{} {} 消息处理服务", wxTypeEnum.getDesc(), wxApp.getWxId());
                this.wxCpAppServices.replace(wxApp.getWxId(), wxMpService);
            } else {
                logger.info("创建微信{} {} 消息处理服务", wxTypeEnum.getDesc(), wxApp.getWxId());
                this.wxCpAppServices.put(wxApp.getWxId(), wxMpService);
            }
        }
        WxCpMessageRouter router = this.getCpMsgRouter(wxMpService);
        if (router != null) {
            if (this.wxCpAppRouters.containsKey(wxApp.getWxId())) {
                logger.info("更新微信{} {} 消息路由", wxTypeEnum.getDesc(), wxApp.getWxId());
                this.wxCpAppRouters.replace(wxApp.getWxId(), router);
            } else {
                logger.info("创建微信{} {} 消息路由", wxTypeEnum.getDesc(), wxApp.getWxId());
                this.wxCpAppRouters.put(wxApp.getWxId(), router);
            }
        }
    }

    /** 初始化小程序的服务 */
    private void initMaService(WxApp wxApp) {
        // TODO 初始化小程序的服务
    }

    private WxMpMessageRouter getMpMsgRouter(WxMpService service) {
        final WxMpMessageRouter router = new WxMpMessageRouter(service);

        // 记录所有事件的日志（异步执行）
        router.rule()
                .handler(this.logHandler)
                .next();

        // 关注
        router.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.SUBSCRIBE)
                .handler(this.msgSubscribeHandler)
                .end();
        // 取消关注
        router.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.UNSUBSCRIBE)
                .handler(this.msgUnsubscribeHandler)
                .end();
        // 扫码
        router.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.SCAN)
                .handler(msgScanHandler)
                .end();
        // 自定义菜单-点击按钮
        router.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.MenuButtonType.CLICK)
                .handler(menuHandler)
                .end();
        // 自定义菜单-点击连接
        router.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.MenuButtonType.VIEW)
                .handler(this.nullHandler)
                .end();
        // 上报地理位置
        router.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.LOCATION)
                .handler(this.msgLocationHandler)
                .end();

        // 接收到地理位置消息
        router.rule().async(false)
                .msgType(WxConsts.XmlMsgType.LOCATION)
                .handler(this.msgLocationHandler)
                .end();
        // 接收到图片消息
        router.rule().async(false)
                .msgType(WxConsts.XmlMsgType.IMAGE)
                .handler(this.msgImageHandler)
                .end();
        // 接收到文本消息
        router.rule().async(false)
                .handler(this.msgTextHandler)
                .end();
        return router;
    }

    private WxCpConfigStorage getCpMsgConfigStorage(WxApp wxApp) {
        return null;
    }

    private WxCpService getCpMsgService(WxCpConfigStorage storage) {
        return null;
    }

    private WxCpMessageRouter getCpMsgRouter(WxCpService service) {
        return null;
    }
}

package com.micropace.ramp.core.service.impl;

import com.micropace.ramp.core.handler.*;
import com.micropace.ramp.base.entity.WxApp;
import com.micropace.ramp.core.service.IWxMsgService;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.constant.WxMpEventConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IWxMsgServiceImpl implements IWxMsgService {
    @Autowired
    protected LogHandler logHandler;
    @Autowired
    protected NullHandler nullHandler;
    @Autowired
    protected KfSessionHandler kfSessionHandler;
    @Autowired
    protected StoreCheckNotifyHandler storeCheckNotifyHandler;
    @Autowired
    private LocationHandler locationHandler;
    @Autowired
    private MenuHandler menuHandler;
    @Autowired
    private MsgHandler msgHandler;
    @Autowired
    private ScanHandler scanHandler;
    @Autowired
    private UnsubscribeHandler unsubscribeHandler;
    @Autowired
    private SubscribeHandler subscribeHandler;

    @Override
    public WxMpConfigStorage getMpMsgConfigStorage(WxApp wxApp) {
        if(wxApp == null) {
            return null;
        }

        WxMpInMemoryConfigStorage configStorage = new WxMpInMemoryConfigStorage();
        configStorage.setAppId(wxApp.getAppId());
        configStorage.setSecret(wxApp.getSecret());
        configStorage.setToken(wxApp.getToken());
        configStorage.setAesKey(wxApp.getAesKey());
        return configStorage;
    }

    @Override
    public WxMpService getMpMsgService(WxMpConfigStorage storage) {
        if(storage == null) {
            return null;
        }

        WxMpService wxMpService = new me.chanjar.weixin.mp.api.impl.WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(storage);
        return wxMpService;
    }

    @Override
    public WxMpMessageRouter getMpMsgRouter(WxMpService service) {
        if(service == null) {
            return null;
        }

        final WxMpMessageRouter newRouter = new WxMpMessageRouter(service);

        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(this.logHandler).next();

        // 接收客服会话管理事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxMpEventConstants.CustomerService.KF_CREATE_SESSION)
                .handler(this.kfSessionHandler)
                .end();
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxMpEventConstants.CustomerService.KF_CLOSE_SESSION)
                .handler(this.kfSessionHandler)
                .end();
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxMpEventConstants.CustomerService.KF_SWITCH_SESSION)
                .handler(this.kfSessionHandler)
                .end();

        // 门店审核事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxMpEventConstants.POI_CHECK_NOTIFY)
                .handler(this.storeCheckNotifyHandler)
                .end();

        // 自定义菜单事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.MenuButtonType.CLICK)
                .handler(this.getMenuHandler())
                .end();

        // 点击菜单连接事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.MenuButtonType.VIEW)
                .handler(this.nullHandler)
                .end();

        // 关注事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.SUBSCRIBE)
                .handler(this.getSubscribeHandler())
                .end();

        // 取消关注事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.UNSUBSCRIBE)
                .handler(this.getUnsubscribeHandler())
                .end();

        // 上报地理位置事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.LOCATION)
                .handler(this.getLocationHandler())
                .end();

        // 接收地理位置消息
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.LOCATION)
                .handler(this.getLocationHandler())
                .end();

        // 扫码事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.SCAN)
                .handler(this.getScanHandler())
                .end();

        // 默认
        newRouter.rule().async(false)
                .handler(this.getMsgHandler())
                .end();

        return newRouter;
    }

    @Override
    public WxCpConfigStorage getCpMsgConfigStorage(WxApp wxApp) {
        return null;
    }

    @Override
    public WxCpService getCpMsgService(WxCpConfigStorage storage) {
        return null;
    }

    @Override
    public WxCpMessageRouter getCpMsgRouter(WxCpService service) {
        return null;
    }

    private MenuHandler getMenuHandler() {
        return this.menuHandler;
    }

    private SubscribeHandler getSubscribeHandler() {
        return this.subscribeHandler;
    }

    private UnsubscribeHandler getUnsubscribeHandler() {
        return this.unsubscribeHandler;
    }

    private AbstractHandler getLocationHandler() {
        return this.locationHandler;
    }

    private MsgHandler getMsgHandler() {
        return this.msgHandler;
    }

    private ScanHandler getScanHandler() {
        return this.scanHandler;
    }
}

package com.micropace.ramp.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import com.micropace.ramp.base.entity.WxApp;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;

/**
 * 微信公众号消息服务管理
 *
 * @author Suffrajet
 */
public interface IWxMsgService {

    /**
     * 获取指定微信公众号(包含订阅号和服务号)的本地存储配置
     *
     * @param wxApp 公众号
     * @return 本地存储配置
     */
    WxMpConfigStorage getMpMsgConfigStorage(WxApp wxApp);

    /**
     * 获取微信公众号(包含订阅号和服务号)的消息服务接口
     *
     * @param storage 公众号的本地存储配置
     * @return 消息服务接口
     */
    WxMpService getMpMsgService(WxMpConfigStorage storage);

    /**
     * 获取微信公众号(包含订阅号和服务号)的消息路由
     *
     * @param service 消息服务接口
     * @return 路由
     */
    WxMpMessageRouter getMpMsgRouter(WxMpService service);

    WxCpConfigStorage getCpMsgConfigStorage(WxApp wxApp);

    WxCpService getCpMsgService(WxCpConfigStorage storage);

    WxCpMessageRouter getCpMsgRouter(WxCpService service);

   // WxMaConfig getWxMaMsgConfigStorage(WxApp wxApp);

   // WxMaService getMaMsgService(WxMaConfig config);

}
